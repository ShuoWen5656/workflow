package top.swzhao.project.workflow.core.runnable;

import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import top.swzhao.project.workflow.common.OmpFlowable;
import top.swzhao.project.workflow.common.contants.EngineConstants;
import top.swzhao.project.workflow.common.contants.FlowKvConstants;
import top.swzhao.project.workflow.common.engine.OmpFlowEngine;
import top.swzhao.project.workflow.common.enums.ProcessStateEnum;
import top.swzhao.project.workflow.common.exception.BeanNotFoundException;
import top.swzhao.project.workflow.common.exception.EngineException;
import top.swzhao.project.workflow.common.exception.ErrorCodes;
import top.swzhao.project.workflow.common.exception.OmpFlowRuntimeException;
import top.swzhao.project.workflow.common.model.bo.FlowParam;
import top.swzhao.project.workflow.common.model.bo.GlobalFlowParam;
import top.swzhao.project.workflow.common.model.bo.TempFlowParam;
import top.swzhao.project.workflow.common.model.dto.FlowProcessSubProcessDto;
import top.swzhao.project.workflow.common.model.dto.FlowSubProcessSortDto;
import top.swzhao.project.workflow.common.model.dto.FlowSubTplDto;
import top.swzhao.project.workflow.common.model.po.FlowProcess;
import top.swzhao.project.workflow.common.model.po.FlowSubTpl;
import top.swzhao.project.workflow.common.model.po.FlowTpl;
import top.swzhao.project.workflow.common.model.po.FlowVariable;
import top.swzhao.project.workflow.common.utils.CommonUtils;
import top.swzhao.project.workflow.core.engineimpl.OmpFlowEngineImpl;
import top.swzhao.project.workflow.core.utils.SpringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author swzhao
 * @date 2023/12/13 8:52 下午
 * @Discreption <>
 */
@Slf4j
public class BaseRunnable implements Runnable{

    /**
     * 全局变量
     */
    protected GlobalFlowParam globalFlowParam;

    /**
     * 临时变量
     */
    protected TempFlowParam tempFlowParam;

    /**
     * initEngine
     */
    protected OmpFlowEngineImpl ompFlowEngine;

    /**
     * 流程信息
     */
    protected FlowProcessSubProcessDto flowProcessSubProcessDto;

    /**
     * 流程模板
     */
    protected FlowTpl flowTpl;

    /**
     * 子模板 id-obj map,用于获取beanName
     */
    protected Map<String, FlowSubTplDto> flowSubTplDtoMap;

    /**
     * 执行线程池
     */
    protected ThreadPoolExecutor threadPoolExecutor;

    /**
     * 动作标识
     */
    protected String action;

    @Override
    public void run() {

    }

    protected void init(GlobalFlowParam globalFlowParam, TempFlowParam tempFlowParam, OmpFlowEngineImpl ompFlowEngine, String action) {
        this.globalFlowParam = globalFlowParam;
        this.tempFlowParam = tempFlowParam;
        this.ompFlowEngine = ompFlowEngine;
        this.threadPoolExecutor = threadPoolExecutor;
        log.info("当前流程动作：{}", action);
        this.action = action;
        log.info("获取当前流程DTO信息...");
        this.flowProcessSubProcessDto = (FlowProcessSubProcessDto) globalFlowParam.getKV(FlowKvConstants.STR_KEY_PROCESS_SUB_PROCESS_DTO);
        log.info("获取成功！当前流程DTO：{}", flowProcessSubProcessDto);
        log.info("加载模板信息...");
        List<FlowSubTplDto> flowSubTplDtos = (List<FlowSubTplDto>) globalFlowParam.getKV(FlowKvConstants.STR_KEY_SUB_TPL_LIST, new TypeReference<List<FlowSubTplDto>>() {
        });
        this.flowTpl = (FlowTpl) globalFlowParam.getKV(FlowKvConstants.STR_KEY_FLOW_TPL);
        this.flowSubTplDtoMap = flowSubTplDtos.stream().collect(Collectors.toMap(FlowSubTpl::getId, u -> u, (k1, k2) -> k1));
        log.info("加载模板信息成功！flowTpl:{}, flowSubTplDtoMap:{}", this.flowTpl, this.flowSubTplDtoMap);
    }

    /**
     * 执行给定的subprocess
     * 接受到异常对subprocess对象进行修改但是不入库
     * @param flowSubProcessSortDto
     */
    protected void executeSubProcess(FlowSubProcessSortDto flowSubProcessSortDto) {
        try {
            String className = flowSubProcessSortDto.getClassName();
            FlowSubTplDto flowSubTplDto = flowSubTplDtoMap.get(flowSubProcessSortDto.getSubTplId());
            String beanName = flowSubTplDto.getName();
            Map<String, OmpFlowable> beansMap = SpringUtils.getBeansFromClazz(OmpFlowable.class);
            OmpFlowable curSubProcess = beansMap.get(beanName);
            if (curSubProcess == null) {
                // 使用className的驼峰规则再获取一次
                beansMap.get(CommonUtils.change2CamelFromClassRef(className));
            }
            if (curSubProcess == null) {
                throw new BeanNotFoundException(ErrorCodes.EXECUTE_ERROR, "未知道执行执行的实例bean");
            }
            switch (flowSubProcessSortDto.getMethodName()) {
                case EngineConstants.STR_VALUE_ENGINE_START:
                    curSubProcess.start(this.tempFlowParam, this.globalFlowParam);
                    flowSubProcessSortDto.setState(String.valueOf(ProcessStateEnum.PROCESS_STATE_FINISH.getCode()));
                case EngineConstants.STR_VALUE_ENGINE_ROLLBACK:
                    curSubProcess.rollback(this.tempFlowParam, this.globalFlowParam);
                    flowSubProcessSortDto.setState(String.valueOf(ProcessStateEnum.PROCESS_STATE_ROLLBACK_FINISH));
                default:
                    break;
            }
        }catch (Exception e) {
            // 客户端程序才能抛出该异常，用来终端引擎执行，并记录错误码
            dealBusinessException(e, flowSubProcessSortDto);
        }
    }

    /**
     * 处理业务异常
     * 1、当前子任务状态修改为失败
     * 2、错误类型为业务类型
     * 3、异常错误码保存
     * 4、判断异常堆栈是否是空，如果不是空说明客户端遇到异常后转为{@link top.swzhao.project.workflow.common.exception.OmpFlowRuntimeException} 给到引擎，堆栈信息应该写客户端遇到的异常
     * 如果异常为空，则说明客户端自行抛出异常，直接存储e的stack即可
     * @param e
     * @param flowSubProcessSortDto
     */
    private void dealBusinessException(Exception e, FlowSubProcessSortDto flowSubProcessSortDto) {
        Integer errorType = null;
        Integer errorCode = null;
        String stackTrace = null;
        if (e instanceof OmpFlowRuntimeException) {
            errorType = EngineConstants.STR_VALUE_ERROR_TYPE_BUSINESS;
            OmpFlowRuntimeException ompFlowRuntimeException = (OmpFlowRuntimeException) e;
            Throwable clientE = ompFlowRuntimeException;
            errorCode = ompFlowRuntimeException.getErrorCode();
            if (ompFlowRuntimeException.getE() != null) {
                // 如果客户端程序已经给了已知的异常，则写特定的异常
                clientE = ompFlowRuntimeException.getE();
            }
            stackTrace = ExceptionUtils.getStackTrace(clientE);
        }else if (e instanceof EngineException) {
            errorType = EngineConstants.STR_VALUE_ERROR_ENGINE;
            EngineException ore = (EngineException) e;
            errorCode = ore.getErrorCode();
            stackTrace = ExceptionUtils.getStackTrace(ore);
        }else {
            errorType = EngineConstants.STR_VALUE_ERROR_UNKNOWN;
            stackTrace = ExceptionUtils.getStackTrace(e);
        }
        flowSubProcessSortDto.setState(String.valueOf(ProcessStateEnum.PROCESS_STATE_FAIL.getCode()));
        flowSubProcessSortDto.setErrorType(errorType);
        flowSubProcessSortDto.setErrorCode(errorCode);
        flowSubProcessSortDto.setErrorStack(stackTrace);
    }


    /**
     * 处理引擎线程的异常
     * @param pme
     */
    protected void dealEngineException(EngineException pme) {
        Integer errorType = EngineConstants.STR_VALUE_ERROR_ENGINE;
        Integer errorCode = pme.getErrorCode();
        String stackTrace = ExceptionUtils.getStackTrace(pme);
        List<FlowSubProcessSortDto> flowSubProcessSortDtos = pme.getFlowSubProcessSortDtos();
        // 处理process
        FlowProcess process = pme.getFlowProcess();
        // 开始类型的动作状态为失败
        if (EngineConstants.listEngineStartAction().contains(this.action)) {
            process.setState(String.valueOf(ProcessStateEnum.PROCESS_STATE_FAIL.getCode()));
        }else if (EngineConstants.listEngineRollBackAction().contains(this.action)) {
            process.setState(String.valueOf(ProcessStateEnum.PROCESS_STATE_ROLLBACK_FAIL.getCode()));
        }
        ompFlowEngine.getFlowHandler().dealProcess(flowSubProcessSortDtos, process);
        // 处理subprocess
        for (FlowSubProcessSortDto flowSubProcessSortDto : flowSubProcessSortDtos) {
            if (EngineConstants.listEngineStartAction().contains(this.action)) {
                flowSubProcessSortDto.setState(ProcessStateEnum.PROCESS_STATE_FAIL.getCode().toString());
            }else if (EngineConstants.listEngineRollBackAction().contains(this.action)) {
                flowSubProcessSortDto.setState(ProcessStateEnum.PROCESS_STATE_ROLLBACK_FAIL.getCode().toString());
            }
            flowSubProcessSortDto.setErrorType(errorType);
            flowSubProcessSortDto.setErrorCode(errorCode);
            flowSubProcessSortDto.setErrorStack(stackTrace);
        }
        ompFlowEngine.getFlowHandler().dealSubProcess(flowSubProcessSortDtos, process);
    }

    private void variablesIntoDB(List<FlowSubProcessSortDto> flowSubProcessSortDtos, FlowProcess flowProcess) {
        // 获取变量快照
        FlowParam gFlowParam = globalFlowParam.getFlowParam();
        FlowParam tflowParam = tempFlowParam.getFlowParam();
        // 入库变量
        List<FlowVariable> flowVariablesForDB = new ArrayList<>();
        if (EngineConstants.listEngineStartAction().contains(this.action)) {
//            convertFlowVariablesForDB()
        }else if (EngineConstants.listEngineRollBackAction().contains(this.action)) {
        }
    }



}
