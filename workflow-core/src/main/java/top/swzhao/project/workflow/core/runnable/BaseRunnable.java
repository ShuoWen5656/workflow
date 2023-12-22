package top.swzhao.project.workflow.core.runnable;

import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import top.swzhao.project.workflow.common.exception.variable.VariableCreateException;
import top.swzhao.project.workflow.common.model.bo.FlowParam;
import top.swzhao.project.workflow.common.model.bo.GlobalFlowParam;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.bo.TempFlowParam;
import top.swzhao.project.workflow.common.model.dto.FlowProcessSubProcessDto;
import top.swzhao.project.workflow.common.model.dto.FlowSubProcessSortDto;
import top.swzhao.project.workflow.common.model.dto.FlowSubTplDto;
import top.swzhao.project.workflow.common.model.dto.Variable;
import top.swzhao.project.workflow.common.model.po.FlowProcess;
import top.swzhao.project.workflow.common.model.po.FlowSubTpl;
import top.swzhao.project.workflow.common.model.po.FlowTpl;
import top.swzhao.project.workflow.common.model.po.FlowVariable;
import top.swzhao.project.workflow.common.service.FlowVariableService;
import top.swzhao.project.workflow.common.utils.CommonUtils;
import top.swzhao.project.workflow.core.engineimpl.OmpFlowEngineImpl;
import top.swzhao.project.workflow.core.utils.SpringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
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
            convertFlowVariablesForDB(gFlowParam, flowVariablesForDB, flowSubProcessSortDtos, EngineConstants.STR_VALUE_VARIABLE_TYPE_GLOBAL);
            convertFlowVariablesForDB(tflowParam, flowVariablesForDB, flowSubProcessSortDtos, EngineConstants.STR_VALUE_VARIABLE_TYPE_TEMP);
        }else if (EngineConstants.listEngineRollBackAction().contains(this.action)) {
            convertFlowVariablesForDB(gFlowParam, flowVariablesForDB, flowSubProcessSortDtos, EngineConstants.STR_VALUE_VARIABLE_TYPE_GLOBAL_ROLLBACK);
            convertFlowVariablesForDB(tflowParam, flowVariablesForDB, flowSubProcessSortDtos, EngineConstants.STR_VALUE_VARIABLE_TYPE_TEMP_ROLLBACK);
        }
        OperResult operResult = ompFlowEngine.getFlowVariableService().batchCreate(flowVariablesForDB);
        if (!operResult.success()) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 失败，入参tableName:{}， 入参：{}， 返回值：{}"), flowVariablesForDB, operResult);
            throw new VariableCreateException(ErrorCodes.VARIABLE_ERROR, "变量入库失败", flowProcess, flowSubProcessSortDtos);
        }
    }

    /**
     * 将flowParam中的变量转为入库的变量列表并放入flowVariableForDB
     * @param flowParam
     * @param flowVariablesForDB
     * @param flowSubProcessSortDtos
     * @param variableType
     */
    private void convertFlowVariablesForDB(FlowParam flowParam, List<FlowVariable> flowVariablesForDB, List<FlowSubProcessSortDto> flowSubProcessSortDtos, Integer variableType) {
        List<FlowVariable> flowVariables = new ArrayList<>();
        Map<String, Variable> input = flowParam.getInput();
        for (String key : input.keySet()) {
            Variable variable = input.get(key);
            FlowVariable flowVariable = new FlowVariable();
            flowVariable.setName(variable.getName());
            flowVariable.setClassType(variable.getClassName());
            flowVariable.setVariableContent(variable.getJsonString());
            flowVariables.add(flowVariable);
        }
        // 给每一个变量分配一个流程，并标识全局变量还是局部变量
        for (FlowSubProcessSortDto flowSubProcessSortDto : flowSubProcessSortDtos) {
            flowVariables.forEach(u -> {
                u.setType(variableType);
                u.setProcessId(flowSubProcessSortDto.getId());
            });
            flowVariablesForDB.addAll(CommonUtils.deepCopyForJson(flowVariables, new TypeReference<List<FlowVariable>>(){}));
        }
    }


    /**
     * 1、更改当前sort的所有子任务状态为执行中并入库
     * 2、更改当前process状态为执行中，当前执行子任务id写入并入库
     * 3、将当前全局变量和局部变量入库一份给没有一个子任务
     * @param flowSubProcessSortDtos
     */
    protected void beforeExecuteForStart(List<FlowSubProcessSortDto> flowSubProcessSortDtos) {
        // 获取流程信息
        FlowProcess flowProcess = flowProcessSubProcessDto.getFlowProcess();
        // 处理三个步骤
        // 1 、处理process
        flowProcess.setState(ProcessStateEnum.PROCESS_STATE_START.getCode().toString());
        List<String> subProcessIds = flowSubProcessSortDtos.stream().map(FlowSubProcessSortDto::getId).collect(Collectors.toList());
        flowProcess.setCurSubId(StringUtils.join(subProcessIds, FlowKvConstants.STR_VALUE_COMMA));
        ompFlowEngine.getFlowHandler().dealSubProcess(flowSubProcessSortDtos, flowProcess);
        // 2、处理subprocess
        for (FlowSubProcessSortDto flowSubProcessSortDto : flowSubProcessSortDtos) {
            flowSubProcessSortDto.setState(ProcessStateEnum.PROCESS_STATE_START.getCode().toString());
            flowSubProcessSortDto.setMethodName(FlowKvConstants.STR_VALUE_START);
        }
        ompFlowEngine.getFlowHandler().dealSubProcess(flowSubProcessSortDtos, flowProcess);
        // 3、变量入库
        variablesIntoDB(flowSubProcessSortDtos, flowProcess);
    }

    /**
     * 执行后收尾
     * 1、检查当前子任务是否全部成功，并将全部的子任务状态和错误（若有）入库
     * 2、如果存在子任务是失败的状态，则将process更改为失败状态并结束线程
     * 3、如果子任务全部成功，则全局变量不变，局部变量input和output调换位置，继续下一顺序执行
     * @param flowSubProcessSortDtos
     * @return
     */
    protected boolean afterExecuteForStart(List<FlowSubProcessSortDto> flowSubProcessSortDtos) {
        FlowProcess flowProcess = flowProcessSubProcessDto.getFlowProcess();
        boolean success = true;
        for (FlowSubProcessSortDto flowSubProcessSortDto : flowSubProcessSortDtos) {
            if (ProcessStateEnum.PROCESS_STATE_FAIL.getCode().equals(flowSubProcessSortDto.getState())) {
                success = false;
            }
        }
        if (success) {
            // 全部成功
            // 交换输入输出
            tempFlowParam.getFlowParam().switchInputOutput();;
        }else {
            // process处理为不成功
            flowProcess.setState(ProcessStateEnum.PROCESS_STATE_FAIL.getCode().toString());
            ompFlowEngine.getFlowHandler().dealProcess(flowSubProcessSortDtos, flowProcess);
        }
        // 处理子process，当前子任务该完成的都已经完成，失败的都已经失败
        ompFlowEngine.getFlowHandler().dealSubProcess(flowSubProcessSortDtos, flowProcess);
        return success;
    }


    /**
     * 执行前准备(回滚)
     * @param flowSubProcessSortDtos
     */
    protected void beforeExecuteForRollback(List<FlowSubProcessSortDto> flowSubProcessSortDtos) {
        // 获取流程管理信息
        FlowProcess flowProcess = flowProcessSubProcessDto.getFlowProcess();
        // 处理三步骤
        // 1、处理process
        List<String> subprocessIds = flowSubProcessSortDtos.stream().map(FlowSubProcessSortDto::getId).collect(Collectors.toList());
        flowProcess.setCurSubId(StringUtils.join(subprocessIds, FlowKvConstants.STR_VALUE_COMMA));
        ompFlowEngine.getFlowHandler().dealProcess(flowSubProcessSortDtos, flowProcess);
        // 2、处理subprocess
        for (FlowSubProcessSortDto flowSubProcessSortDto : flowSubProcessSortDtos) {
            flowSubProcessSortDto.setMethodName(FlowKvConstants.STR_VALUE_ROLLBACK);
        }
        ompFlowEngine.getFlowHandler().dealSubProcess(flowSubProcessSortDtos, flowProcess);
        // 3、变量入库
        variablesIntoDB(flowSubProcessSortDtos, flowProcess);
    }


    /**
     * 执行后收尾（回滚）
     * @param flowSubProcessSortDtos
     * @return
     */
    protected boolean afterExecuteForRollback(List<FlowSubProcessSortDto> flowSubProcessSortDtos) {
        FlowProcess flowProcess = flowProcessSubProcessDto.getFlowProcess();
        boolean success = true;
        for (FlowSubProcessSortDto flowSubProcessSortDto : flowSubProcessSortDtos) {
            if (ProcessStateEnum.PROCESS_STATE_ROLLBACK_FAIL.getCode().equals(flowSubProcessSortDto.getState())) {
                success = false;
            }
        }
        if (success) {
            // 全部成功
            // 交换输入输出
            tempFlowParam.getFlowParam().switchInputOutput();;
        }else {
            // process处理为不成功
            flowProcess.setState(ProcessStateEnum.PROCESS_STATE_ROLLBACK_FAIL.getCode().toString());
            ompFlowEngine.getFlowHandler().dealProcess(flowSubProcessSortDtos, flowProcess);
        }
        // 处理子process，当前子任务该完成的都已经完成，失败的都已经失败
        ompFlowEngine.getFlowHandler().dealSubProcess(flowSubProcessSortDtos, flowProcess);
        return success;
    }


    protected void execute(List<FlowSubProcessSortDto> flowSubProcessSortDtos) throws InterruptedException {
        if (flowSubProcessSortDtos.size() == FlowKvConstants.NUM_VALUE_I) {
            FlowSubProcessSortDto flowSubProcessSortDto = flowSubProcessSortDtos.get(FlowKvConstants.NUM_VALUE_O);
            // 如果只有一个任务的时候，使用当前主线程执行即可
            executeSubProcess(flowSubProcessSortDto);
        }else {
            // 多任务状况，同步原子值
            CountDownLatch countDownLatch = new CountDownLatch(flowSubProcessSortDtos.size());
            for (FlowSubProcessSortDto flowSubProcessSortDto : flowSubProcessSortDtos) {
                this.threadPoolExecutor.submit(() -> {
                   try {

                   }catch (Exception e) {
                       // 异步任务的异常打印
                       log.error("异步任务执行异常:", e);
                   }finally {
                       countDownLatch.countDown();
                   }
                });
            }
            // 等待所有线程结束
            countDownLatch.await();
        }
    }






}
