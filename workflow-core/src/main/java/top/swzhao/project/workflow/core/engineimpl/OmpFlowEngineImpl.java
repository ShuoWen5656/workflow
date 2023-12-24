package top.swzhao.project.workflow.core.engineimpl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import top.swzhao.project.workflow.common.contants.EngineConstants;
import top.swzhao.project.workflow.common.contants.FlowKvConstants;
import top.swzhao.project.workflow.common.engine.OmpFlowEngine;
import top.swzhao.project.workflow.common.enums.ProcessStateEnum;
import top.swzhao.project.workflow.common.exception.ErrorCodes;
import top.swzhao.project.workflow.common.exception.OmpFlowException;
import top.swzhao.project.workflow.common.exception.process.ProcessCreateException;
import top.swzhao.project.workflow.common.exception.process.ProcessException;
import top.swzhao.project.workflow.common.exception.process.ProcessStateException;
import top.swzhao.project.workflow.common.exception.template.TemplateNotFoundException;
import top.swzhao.project.workflow.common.handler.FlowHandler;
import top.swzhao.project.workflow.common.model.bo.GlobalFlowParam;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.bo.TempFlowParam;
import top.swzhao.project.workflow.common.model.dto.FlowProcessSubProcessDto;
import top.swzhao.project.workflow.common.model.dto.FlowSubProcessSortDto;
import top.swzhao.project.workflow.common.model.dto.FlowSubTplDto;
import top.swzhao.project.workflow.common.model.po.FlowProcess;
import top.swzhao.project.workflow.common.model.po.FlowSubProcess;
import top.swzhao.project.workflow.common.model.po.FlowTpl;
import top.swzhao.project.workflow.common.service.*;
import top.swzhao.project.workflow.common.utils.ConvertUtils;
import top.swzhao.project.workflow.core.runnable.BaseRunnable;
import top.swzhao.project.workflow.core.runnable.RollbackRunnable;
import top.swzhao.project.workflow.core.runnable.StartRunnable;
import top.swzhao.project.workflow.core.utils.ExecutorPoolUtils;
import top.swzhao.project.workflow.core.utils.SpringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author swzhao
 * @date 2023/12/11 9:33 下午
 * @Discreption <>
 */
@Service
@Slf4j
public class OmpFlowEngineImpl implements OmpFlowEngine {

    @Autowired
    private SpringUtils springUtils;

    @Autowired
    private FlowDicService flowDicService;

    @Autowired
    private FlowProcessService flowProcessService;

    @Autowired
    private FlowSubProcessService flowSubProcessService;

    @Autowired
    private FlowSubTplService flowSubTplService;

    @Autowired
    private FlowTplService flowTplService;

    @Autowired
    private FlowTplSubClassNameService flowTplSubClassNameService;

    @Autowired
    private FlowVariableService flowVariableService;

    @Autowired
    private FlowHandler flowHandler;

    /**
     * 自己的事务管理器
     */
    @Qualifier(FlowKvConstants.STR_KEY_TRANSACTION_MANAGER_FLOW)
    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    /**
     * 1、取模板、判断模板是否存在
     * 2、去关联子任务实体，校验顺序
     * 3、将任务列表和流程信息准备到全局变量中供给所有流程使用，创建流程和子流程
     * 4、将子任务列表作为整体交给线程池，将变量准备完成后交给线程池
     * 5、线程池循序执行子任务
     * @param tplId
     * @param variables variables中object必须存在get和set方法
     * @return
     * @throws OmpFlowException
     */
    @Override
//    @Transactional(rollbackFor = Exception.class, transactionManager = FlowKvConstants.STR_KEY_TRANSACTION_MANAGER_FLOW)
    public OperResult start(String tplId, Map<String, Object> variables) throws OmpFlowException {
        try {
            // 手动事务控制:由于异步任务查库必须在事务提交之后才能继续执行，所以这里使用手动事务，并且手动回滚
            DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
            defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus transaction = dataSourceTransactionManager.getTransaction(defaultTransactionDefinition);

            HashMap<String, Object> globalParamMap = new HashMap<>(16);
            OperResult<FlowTpl> tplOperResult = flowTplService.getTplById(tplId);
            if (!tplOperResult.success() || Objects.isNull(tplOperResult.getData())) {
                log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 获取模板失败， 入参:tplId:{}, 返回值: tplOperResult:{}"), tplId, tplOperResult);
                throw new TemplateNotFoundException(ErrorCodes.TEMPLATE_ERROR, "获取模板失败");
            }
            FlowTpl flowTpl = tplOperResult.getData();
            // 检查子任务是否存在，子任务顺序是否正常，最后返回已经排序号的子任务
            List<FlowSubTplDto> flowSubTplDtoList = checkSubTpl(flowTpl);
            log.info("实例化流程中......");
            // 流程容器，入库后存储在dto中
            FlowProcessSubProcessDto flowProcessSubProcessDto = new FlowProcessSubProcessDto();
            dealProcessAndSubProcess(flowTpl, flowSubTplDtoList, flowProcessSubProcessDto);
            log.info("实例化流程成功");
            // 准备全局变量
            GlobalFlowParam<Object> globalFlowParam = new GlobalFlowParam<>();
            // 准备临时变量
            TempFlowParam tempFlowParam = new TempFlowParam();
            globalParamMap.put(FlowKvConstants.STR_KEY_FLOW_TPL, flowTpl);
            globalParamMap.put(FlowKvConstants.STR_KEY_SUB_TPL_LIST, flowSubTplDtoList);
            globalParamMap.put(FlowKvConstants.STR_KEY_PROCESS_SUB_PROCESS_DTO, flowProcessSubProcessDto);
            flowHandler.dealFlowParam(globalFlowParam.getFlowParam(), globalParamMap, EngineConstants.STR_VALUE_VARIABLE_TYPE_GLOBAL, flowProcessSubProcessDto);
            flowHandler.dealFlowParam(tempFlowParam.getFlowParam(), variables, EngineConstants.STR_VALUE_VARIABLE_TYPE_TEMP, flowProcessSubProcessDto);
            // 初始化主线程，提交
            StartRunnable startRunnable = new StartRunnable(globalFlowParam, tempFlowParam, this, EngineConstants.STR_VALUE_ENGINE_START);
            ThreadPoolExecutor pool = ExecutorPoolUtils.getPool();
            // 提交线程之前提交一次事务，放置线程异步调用拿不到数据
            dataSourceTransactionManager.commit(transaction);
            // 提交异步任务
            pool.submit(startRunnable);
            return new OperResult(OperResult.OPT_SUCCESS, "任务提交成功");
        } catch (ProcessCreateException pce) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参tplId:{}， variables：{}， 结果：{}"), tplId, variables, pce);
            // 实例化流程失败，需要回滚删除所有的记录
            String processId = pce.getProcessId();
            // 回滚删除流程并删除子流程
            if (StringUtils.isNoneBlank(processId)) {
                rollbackProcessAndSubProcess(processId);
            }
            return new OperResult(OperResult.OPT_FAIL, "引擎示例化流程失败");
        } catch (OmpFlowException oe) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参tplId:{}， variables：{}， 结果：{}"), tplId, variables, oe);
            throw oe;
        }catch (Exception e){
            // 未知异常，需要将信息尽可能详细的打印出来
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参tplId:{}， variables：{}， 结果：{}"), tplId, variables, e);
            return new OperResult(OperResult.OPT_FAIL, "执行异常");
        }
    }


    /**
     * 1、判断流程是否存在，从库里取出来
     * 2、根据流程获取所属模板，并检查模板
     * 3、根据流程获取其他的子模板，并检查
     * 4、更新状态和清空错误信息
     * 5、准备全局变量和临时变量
     * 6、提交start线程
     * @param processId
     * @param variables
     * @return
     * @throws OmpFlowException
     */
    @Override
    public OperResult reStart(String processId, Map<String, Object> variables) throws OmpFlowException {
        try {
            commonDealProcess(processId, variables, EngineConstants.STR_VALUE_ENGINE_RE_START);
            return new OperResult(OperResult.OPT_SUCCESS, "重新执行任务成功");
        }catch (OmpFlowException oe) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参processId:{}， variables：{}， 结果：{}"), processId, variables, oe);
            throw oe;
        } catch (Exception e) {
            // 未知异常，需要将信息尽可能详细的打印出来
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参tplId:{}， variables：{}， 结果：{}"), processId, variables, e);
            return new OperResult(OperResult.OPT_FAIL, "执行异常");
        }
    }



    @Override
    public OperResult reRun(String processId, Map<String, Object> variables) throws OmpFlowException {
        try {
            commonDealProcess(processId, variables, EngineConstants.STR_VALUE_ENGINE_RE_RUN);
            return new OperResult(OperResult.OPT_SUCCESS, "执行成功");
        }catch (OmpFlowException oe) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参processId:{}， variables：{}， 结果：{}"), processId, variables, oe);
            throw oe;
        } catch (Exception e) {
            // 未知异常，需要将信息尽可能详细的打印出来
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参processId:{}， variables：{}， 结果：{}"), processId, variables, e);
            return new OperResult(OperResult.OPT_FAIL, "执行异常");
        }
    }

    @Override
    public OperResult skipCurrentAndRun(String processId, Map<String, Object> variables) throws OmpFlowException {
        try {
            commonDealProcess(processId, variables, EngineConstants.STR_VALUE_ENGINE_SKIP);
            return new OperResult(OperResult.OPT_SUCCESS, "执行成功");
        }catch (OmpFlowException oe) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参processId:{}， variables：{}， 结果：{}"), processId, variables, oe);
            throw oe;
        } catch (Exception e) {
            // 未知异常，需要将信息尽可能详细的打印出来
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参processId:{}， variables：{}， 结果：{}"), processId, variables, e);
            return new OperResult(OperResult.OPT_FAIL, "执行异常");
        }
    }

    @Override
    public OperResult rollback(String processId, Map<String, Object> variables) throws OmpFlowException {
        try {
            commonDealProcess(processId, variables, EngineConstants.STR_VALUE_ENGINE_ROLLBACK);
            return new OperResult(OperResult.OPT_SUCCESS, "执行成功");
        }catch (OmpFlowException oe) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参processId:{}， variables：{}， 结果：{}"), processId, variables, oe);
            throw oe;
        } catch (Exception e) {
            // 未知异常，需要将信息尽可能详细的打印出来
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参processId:{}， variables：{}， 结果：{}"), processId, variables, e);
            return new OperResult(OperResult.OPT_FAIL, "执行异常");
        }
    }

    @Override
    public OperResult rollbackSubProcess(String processId, Map<String, Object> variables) throws OmpFlowException {
        try {
            commonDealProcess(processId, variables, EngineConstants.STR_VALUE_ENGINE_ROLLBACK);
            return new OperResult(OperResult.OPT_SUCCESS, "执行成功");
        }catch (OmpFlowException oe) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参processId:{}， variables：{}， 结果：{}"), processId, variables, oe);
            throw oe;
        } catch (Exception e) {
            // 未知异常，需要将信息尽可能详细的打印出来
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参processId:{}， variables：{}， 结果：{}"), processId, variables, e);
            return new OperResult(OperResult.OPT_FAIL, "执行异常");
        }
    }


    /*******************************************************************************************************************
     *                                                     公共代码块                                                   *
     *******************************************************************************************************************/

    private void commonDealProcess(String processId, Map<String, Object> variables, String action) throws OmpFlowException {
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus transaction = dataSourceTransactionManager.getTransaction(defaultTransactionDefinition);
        try {
            // 获取process示例、模板、子模板
            Map<String, Object> map = getProcessTplsByProcessId(processId);
            log.info("回滚流程id：{}", processId);
            FlowProcess flowProcess = (FlowProcess) map.get(FlowKvConstants.STR_KEY_FLOWPROCESS);
            FlowTpl flowTpl = (FlowTpl) map.get(FlowKvConstants.STR_KEY_FLOW_TPL);
            List<FlowSubTplDto> flowSubTplDtos = (List<FlowSubTplDto>) map.get(FlowKvConstants.STR_KEY_SUB_TPL_DTOS);
            log.info("回滚流程信息：flowProcess:{}, flowTpl:{}, flowSubTplDtos:{}", flowProcess, flowTpl, flowSubTplDtos);
            checkProcessState(flowProcess);
            List<FlowSubProcessSortDto> flowSubProcessSortDtos = new ArrayList<>();
            FlowSubProcess condition;
            OperResult<List<FlowSubProcess>> subProcessResult;
            List<FlowSubProcess> flowSubProcesses;
            switch (action) {
                case EngineConstants.STR_VALUE_ENGINE_ROLLBACK_SUB_PROCESS:
                case EngineConstants.STR_VALUE_ENGINE_ROLLBACK:
                    // 回滚子任务和回滚任务逻辑基本相同
                    // 子任务，倒序获取
                    log.info("获取子流程列表（倒序）...");
                    condition = new FlowSubProcess();
                    condition.setProcessId(processId);
                    subProcessResult = flowSubProcessService.listSubProcessByCondition(condition, FlowKvConstants.STR_KEY_DESC);
                    flowSubProcesses = subProcessResult.getData();
                    log.info("获取子流程列表成功 flowSubProcesses:{}, ", flowSubProcesses);
                    // rollback要条件检测
                    flowSubProcessSortDtos = commonCheck(flowProcess, flowSubProcesses, flowSubTplDtos, action);
                    log.info("将要回滚的任务列表：flowSubProcessSortDtos:{}", flowSubProcessSortDtos);
                    // 更新状态和清空错误信息
                    initProcessAndSubProcess(flowProcess, flowSubProcessSortDtos, action);
                    break;
                case EngineConstants.STR_VALUE_ENGINE_RE_START:
                case EngineConstants.STR_VALUE_ENGINE_RE_RUN:
                case EngineConstants.STR_VALUE_ENGINE_SKIP:
                    log.info("获取子流程列表（正序）...");
                    condition = new FlowSubProcess();
                    condition.setProcessId(processId);
                    subProcessResult = flowSubProcessService.listSubProcessByCondition(condition, FlowKvConstants.STR_KEY_ASC);
                    if (!subProcessResult.success()) {
                        log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 执行失败，获取子任务列表失败，入参：condition：{}, 返回值"), condition, subProcessResult);
                        throw new ProcessException(ErrorCodes.PROCESS_ERROR, "获取子任务失败");
                    }
                    flowSubProcesses = subProcessResult.getData();
                    log.info("获取子流程列表成功（正序）：flowSubProcesss:{}", flowSubProcesses);
                    flowSubProcessSortDtos = commonCheck(flowProcess, flowSubProcesses, flowSubTplDtos, action);
                    initProcessAndSubProcess(flowProcess, flowSubProcessSortDtos, action);
                    break;
                default:
                    break;
            }
            // 准备变量
            Map<String, Object> globalParamMap = new HashMap<>(16);
            GlobalFlowParam globalFlowParam = new GlobalFlowParam();
            TempFlowParam tempFlowParam = new TempFlowParam();
            FlowProcessSubProcessDto flowProcessSubProcessDto = new FlowProcessSubProcessDto(flowProcess, flowSubProcessSortDtos);
            globalParamMap.put(FlowKvConstants.STR_KEY_FLOW_TPL, flowTpl);
            globalParamMap.put(FlowKvConstants.STR_KEY_SUB_TPL_LIST, flowSubTplDtos);
            globalParamMap.put(FlowKvConstants.STR_KEY_PROCESS_SUB_PROCESS_DTO, flowProcessSubProcessDto);
            ThreadPoolExecutor pool = ExecutorPoolUtils.getPool();
            // 回滚走回滚变量，正向走正向变量
            if (EngineConstants.listEngineRollBackAction().contains(action)) {
                // 准备全局变量
                flowHandler.dealFlowParam(globalFlowParam.getFlowParam(), globalParamMap, EngineConstants.STR_VALUE_VARIABLE_TYPE_GLOBAL_ROLLBACK, flowProcessSubProcessDto);
                // 准备局部变量
                flowHandler.dealFlowParam(tempFlowParam.getFlowParam(), variables, EngineConstants.STR_VALUE_VARIABLE_TYPE_TEMP_ROLLBACK, flowProcessSubProcessDto);
            }else {
                // 准备全局变量
                flowHandler.dealFlowParam(globalFlowParam.getFlowParam(), globalParamMap, EngineConstants.STR_VALUE_VARIABLE_TYPE_GLOBAL, flowProcessSubProcessDto);
                // 准备局部变量
                flowHandler.dealFlowParam(tempFlowParam.getFlowParam(), variables, EngineConstants.STR_VALUE_VARIABLE_TYPE_TEMP, flowProcessSubProcessDto);
            }
            BaseRunnable baseRunnable = new RollbackRunnable(globalFlowParam, tempFlowParam, this, action);
            dataSourceTransactionManager.commit(transaction);
            // 提交事务
            pool.submit(baseRunnable);
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，事务回滚"));
            dataSourceTransactionManager.rollback(transaction);
            throw e;
        }
    }

    /**
     * 检查当前流程是否在执行中
     * @param flowProcess
     */
    private void checkProcessState(FlowProcess flowProcess) throws ProcessStateException {
        if (ProcessStateEnum.listActiveState().contains(flowProcess.getState())) {
            throw new ProcessStateException(ErrorCodes.PROCESS_STATE_ERROR, String.format("当前process状态为 \"%s\", 不可在此执行操作", ProcessStateEnum.getNameByCode(Integer.valueOf(flowProcess.getState()))));
        }
    }

    /**
     *
     * @param flowProcess
     * @param flowSubProcesses
     * @param flowSubTplDtos
     * @param action
     * @return
     */
    private List<FlowSubProcessSortDto> commonCheck(FlowProcess flowProcess, List<FlowSubProcess> flowSubProcesses, List<FlowSubTplDto> flowSubTplDtos, String action) throws TemplateNotFoundException, ProcessStateException {
        if (EngineConstants.STR_VALUE_ENGINE_RE_START.equals(action)) {
            // restart不需要失败条件
            return ConvertUtils.convert2FlowSubProcessSortDto(flowSubProcesses, flowSubTplDtos);
        }
        if (!ProcessStateEnum.PROCESS_STATE_FAIL.getCode().equals(flowProcess.getState())) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 检测到当前process状态异常，入参，Process:{}"), flowProcess);
            throw new ProcessStateException(ErrorCodes.PROCESS_STATE_ERROR, "流程状态不是失败状态");
        }
        // 需要执行的子任务列表
        List<FlowSubProcess> skipRunSubProcess = new ArrayList<>();
        List<FlowSubProcess> failSubProcess = new ArrayList<>();
        // 失败节点顺序
        Integer failSort = FlowKvConstants.NUM_VALUE_NEGATIVE_1;
        for (FlowSubProcess flowSubProcess : flowSubProcesses) {
            if (ProcessStateEnum.PROCESS_STATE_FAIL.getCode().toString().equals(flowSubProcess.getState())) {
                failSubProcess.add(flowSubProcess);
            }
            // 找到第一个失败的顺序节点
            if (ProcessStateEnum.PROCESS_STATE_FAIL.getCode().toString().equals(flowProcess.getState()) && failSort == FlowKvConstants.NUM_VALUE_NEGATIVE_1) {
                // 当前子任务失败并且未找到失败顺序节点
                failSort = Integer.parseInt(flowSubProcess.getSort());
            }
            if (EngineConstants.listEngineRollBackAction().contains(action)) {
                if (ProcessStateEnum.PROCESS_STATE_ROLLBACK_FAIL.getCode().toString().equals(flowProcess.getState()) && Integer.parseInt(flowSubProcess.getSort()) < failSort) {
                    // 出现两个顺序点存在失败流程
                    log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 检测到存在两个顺序点为失败状态，入参，Process:{}"), flowProcess);
                    throw new ProcessStateException(ErrorCodes.PROCESS_STATE_ERROR, "存在多个失败的顺序节点");
                }
            }else {
                if (ProcessStateEnum.PROCESS_STATE_FAIL.getCode().equals(flowSubProcess.getState()) && Integer.parseInt(flowSubProcess.getSort()) > failSort) {
                    // 出现两个顺序点存在失败流程
                    log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 检测到存在两个顺序点为失败状态，入参，Process:{}"), flowProcess);
                    throw new ProcessStateException(ErrorCodes.PROCESS_STATE_ERROR, "存在多个失败的顺序节点");                }
            }
            switch (action) {
                case EngineConstants.STR_VALUE_ENGINE_ROLLBACK:
                    if (Integer.parseInt(flowSubProcess.getSort()) <= failSort) {
                        // 在失败节点以及管连接点之前的节点需要回滚
                        skipRunSubProcess.add(flowSubProcess);
                    }
                    break;
                case EngineConstants.STR_VALUE_ENGINE_ROLLBACK_SUB_PROCESS:
                    if (Integer.parseInt(flowSubProcess.getSort()) == failSort) {
                        skipRunSubProcess.add(flowSubProcess);
                    }
                    break;
                case EngineConstants.STR_VALUE_ENGINE_RE_START:
                    skipRunSubProcess.add(flowSubProcess);
                    break;
                case EngineConstants.STR_VALUE_ENGINE_RE_RUN:
                    if (Integer.parseInt(flowSubProcess.getSort()) >= failSort) {
                        skipRunSubProcess.add(flowSubProcess);
                    }
                    break;
                case EngineConstants.STR_VALUE_ENGINE_SKIP:
                    if (Integer.parseInt(flowSubProcess.getSort()) > failSort) {
                        skipRunSubProcess.add(flowSubProcess);
                    }
                    break;
                default:
                    break;
            }
        }
        // 将失败的子任务全部修改为已经跳过，但是保留错误堆栈
        if (EngineConstants.STR_VALUE_ENGINE_SKIP.equals(action)) {
            List<FlowSubProcessSortDto> failDtos = ConvertUtils.convert2FlowSubProcessSortDto(failSubProcess, flowSubTplDtos);
            failDtos.forEach(u -> u.setState(ProcessStateEnum.PROCESS_STATE_SKIP.getCode().toString()));
            flowHandler.dealSubProcess(failDtos, flowProcess);
        }
        return ConvertUtils.convert2FlowSubProcessSortDto(skipRunSubProcess, flowSubTplDtos);
    }


    /**
     * 根据processId获取process实体，对应模板，对应子模板
     * @param processId
     * @return
     */
    private Map<String, Object> getProcessTplsByProcessId(String processId) throws ProcessException, TemplateNotFoundException {
        Map<String, Object> map = new HashMap<>(16);
        // 获取流程
        OperResult<FlowProcess> processOperResult = flowProcessService.getById(processId);
        if (!processOperResult.success()) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat("失败 流程不存在，入参，Process:{}"), processId);
            throw new ProcessException(ErrorCodes.PROCESS_ERROR, "流程不存在");
        }
        FlowProcess flowProcess = processOperResult.getData();
        map.put(FlowKvConstants.STR_KEY_FLOWPROCESS, flowProcess);
        String tplId = flowProcess.getTplId();
        // 获取对应模板
        OperResult<FlowTpl> flowTplOperResult = flowTplService.getTplById(tplId);
        if (!flowTplOperResult.success()) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat("失败 流程对应模板不存在，入参，Process:{}"), processId);
            throw new ProcessException(ErrorCodes.PROCESS_ERROR, "流程对应模板不存在");
        }
        FlowTpl flowTpl = flowTplOperResult.getData();
        map.put(FlowKvConstants.STR_KEY_FLOW_TPL, flowTpl);
        // 获取模板对应子模板并排序返回
        List<FlowSubTplDto> flowSubTplDtos = checkSubTpl(flowTpl);
        map.put(FlowKvConstants.STR_KEY_SUB_TPL_DTOS, flowSubTplDtos);
        return map;
    }


    /**
     * 初始化过程和子过程
     * @param flowProcess
     * @param flowSubProcessSortDtos
     * @param action
     */
    private void initProcessAndSubProcess(FlowProcess flowProcess, List<FlowSubProcessSortDto> flowSubProcessSortDtos, String action) {
        switch (action) {
            case EngineConstants.STR_VALUE_ENGINE_ROLLBACK:
            case EngineConstants.STR_VALUE_ENGINE_ROLLBACK_SUB_PROCESS:
                flowProcess.setState(ProcessStateEnum.PROCESS_STATE_ROLLBACK.getCode().toString());
                flowProcess.setCurSubId(FlowKvConstants.STR_VALUE_EMP);
                flowSubProcessSortDtos.forEach(u -> {
                    u.setState(ProcessStateEnum.PROCESS_STATE_ROLLBACK.getCode().toString());
                    u.setErrorType(FlowKvConstants.NUM_VALUE_NEGATIVE_1);
                    u.setErrorStack(FlowKvConstants.STR_VALUE_EMP);
                    u.setErrorCode(FlowKvConstants.NUM_VALUE_O);
                });
                break;
            case EngineConstants.STR_VALUE_ENGINE_RE_START:
            case EngineConstants.STR_VALUE_ENGINE_RE_RUN:
            case EngineConstants.STR_VALUE_ENGINE_SKIP:
                flowProcess.setState(ProcessStateEnum.PROCESS_STATE_UNSTART.getCode().toString());
                flowProcess.setCurSubId(FlowKvConstants.STR_VALUE_EMP);
                flowSubProcessSortDtos.forEach(u -> {
                    u.setState(ProcessStateEnum.PROCESS_STATE_UNSTART.getCode().toString());
                    if (!EngineConstants.STR_VALUE_ENGINE_SKIP.equals(action)) {
                        u.setErrorType(FlowKvConstants.NUM_VALUE_NEGATIVE_1);
                        u.setErrorStack(FlowKvConstants.STR_VALUE_EMP);
                        u.setErrorCode(FlowKvConstants.NUM_VALUE_O);
                    }
                });
                break;
            default:
                break;
        }
        flowHandler.dealProcess(flowSubProcessSortDtos, flowProcess);
        flowHandler.dealSubProcess(flowSubProcessSortDtos, flowProcess);
    }





    private List<FlowSubTplDto> checkSubTpl(FlowTpl flowTpl) throws TemplateNotFoundException {
        OperResult<List<FlowSubTplDto>> listOperResult = flowTplSubClassNameService.listFlowSubTplByTplId(flowTpl.getId());
        if (!listOperResult.success() || CollectionUtils.isEmpty(listOperResult.getData())) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 获取子模板失败，入参flowTpl:{}， 结果：{}"), flowTpl, listOperResult);
            throw new TemplateNotFoundException(ErrorCodes.TEMPLATE_ERROR, "获取子模板失败");
        }
        List<FlowSubTplDto> flowSubTplDtos = listOperResult.getData();
        // 检查排序是否正确，如果存在1111，5的情况，则1111四个任务会并行，第五个任务顺序应该为5
        for (FlowSubTplDto flowSubTplDto : flowSubTplDtos) {
            if (flowSubTplDto.getSort() <= FlowKvConstants.NUM_VALUE_O || flowSubTplDto.getSort() > flowSubTplDtos.size()) {
                log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 存在子模板配置顺序在（0， {}）]之外"), flowSubTplDtos.size());
                throw new TemplateNotFoundException(ErrorCodes.TEMPLATE_ERROR, "子模板顺序有误");
            }
        }
        // 按照sort排序，从小到大，o1是后面的元素，o2为前一个元素
        flowSubTplDtos.sort(new Comparator<FlowSubTplDto>() {
            @Override
            public int compare(FlowSubTplDto o1, FlowSubTplDto o2) {
                if (o1.getSort() < o2.getSort()) {
                    return FlowKvConstants.NUM_VALUE_NEGATIVE_1;
                }else if (o1.getSort() > o2.getSort()) {
                    return FlowKvConstants.NUM_VALUE_I;
                }else {
                    return FlowKvConstants.NUM_VALUE_O;
                }
            }
        });
        return flowSubTplDtos;
    }


    /**
     * 创建process示例和subprocess子示例到库中
     * @param flowTpl
     * @param flowSubTplDtoList
     * @param flowProcessSubProcessDto
     */
    private void dealProcessAndSubProcess(FlowTpl flowTpl, List<FlowSubTplDto> flowSubTplDtoList, FlowProcessSubProcessDto flowProcessSubProcessDto) throws ProcessCreateException {
        String processUUID = UUID.randomUUID().toString();
        // 实例化流程
        FlowProcess flowProcess = new FlowProcess(processUUID, String.format(FlowKvConstants.STR_CONTAIN_PROCESS_NAME, flowTpl.getName(), processUUID), flowTpl.getId(), String.valueOf(FlowKvConstants.NUM_VALUE_O));
        flowProcessSubProcessDto.setFlowProcess(flowProcess);
        OperResult processCreateResult = flowProcessService.createProcess(flowProcess);
        if (!processCreateResult.success()) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 创建流程失败，入参:{}, 结果：{}"), flowProcess, processCreateResult);
            throw new ProcessCreateException(ErrorCodes.PROCESS_ERROR, "创建流程失败", processUUID);
        }
        // 示例化子流程
        List<FlowSubProcessSortDto> flowSubProcessSortDtos = new ArrayList<>();
        // 封装子流程列表
        for (FlowSubTplDto flowSubTplDto : flowSubTplDtoList) {
            String subProcessUUID = UUID.randomUUID().toString();
            FlowSubProcessSortDto flowSubProcessSortDto = new FlowSubProcessSortDto(subProcessUUID, String.format(FlowKvConstants.STR_CONTAIN_PROCESS_NAME, flowSubTplDto.getName(), subProcessUUID),
                    flowSubTplDto.getId(), String.valueOf(FlowKvConstants.NUM_VALUE_O), processUUID, String.valueOf(flowSubTplDto.getSort()), flowSubTplDto.getName(), flowSubTplDto.getClassName());
            flowSubProcessSortDtos.add(flowSubProcessSortDto);
        }
        // 批量创建子流程
        OperResult operResult = flowSubProcessService.batchCreate(flowSubProcessSortDtos);
        flowProcessSubProcessDto.setFlowSubProcessSortDtoList(flowSubProcessSortDtos);
        if (!operResult.success()) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 创建子流程失败，入参 flowSubProcessSortDtos :{}, 结果 operResult：{}"), flowSubProcessSortDtos, operResult);
            throw new ProcessCreateException(ErrorCodes.PROCESS_ERROR, "子流程实例化失败", processUUID);
        }
    }

    private void rollbackProcessAndSubProcess(String processId) {
        // 删除子流程，因为资料流程关联主流程，无法直接删除
        OperResult deleteOperResult = flowSubProcessService.deleteByProcessId(processId);
        if (!deleteOperResult.success()) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 回滚流程和子流程失败：删除子流程失败，入参processId:{}， 结果：{}"), processId, deleteOperResult);
        }
        OperResult operResult = flowProcessService.deleteById(processId);
        if (!operResult.success()) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 回滚流程和子流程失败：删除流程失败，入参processId:{}， 结果：{}"), processId, operResult);
        }
    }


    /**
     * 初始化过程和子过程
     * @param flowProcess
     * @param flowSubProcessSortDtos
     */
    private void initProcessAndSubProcessForSkip(FlowProcess flowProcess, List<FlowSubProcessSortDto> flowSubProcessSortDtos) {
        // process修改为未开始
        flowProcess.setState(ProcessStateEnum.PROCESS_STATE_UNSTART.getCode().toString());
        flowProcess.setCurSubId(FlowKvConstants.STR_VALUE_EMP);
        flowHandler.dealProcess(flowSubProcessSortDtos, flowProcess);
        // subprocess修该为未开始单不清楚错误信息
        flowSubProcessSortDtos.forEach(u -> {
            u.setState(ProcessStateEnum.PROCESS_STATE_UNSTART.getCode().toString());
        });
        flowHandler.dealSubProcess(flowSubProcessSortDtos, flowProcess);
    }



    @Override
    public FlowTplService getFlowTplService() {
        return flowTplService;
    }

    @Override
    public FlowSubTplService getFlowSubTplService() {
        return flowSubTplService;
    }

    @Override
    public FlowTplSubClassNameService getFlowTplSubClassNameService() {
        return flowTplSubClassNameService;
    }

    @Override
    public FlowProcessService getFlowProcessService() {
        return flowProcessService;
    }

    @Override
    public FlowSubProcessService getFlowSubProcessService() {
        return flowSubProcessService;
    }

    @Override
    public FlowDicService getFlowDicService() {
        return flowDicService;
    }

    @Override
    public FlowVariableService getFlowVariableService() {
        return flowVariableService;
    }


    public SpringUtils getSpringUtils() {
        return springUtils;
    }

    public FlowHandler getFlowHandler() {
        return flowHandler;
    }

}
