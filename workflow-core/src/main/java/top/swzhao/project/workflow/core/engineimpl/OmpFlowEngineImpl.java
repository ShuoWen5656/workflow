package top.swzhao.project.workflow.core.engineimpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.swzhao.project.workflow.common.contants.FlowKvConstants;
import top.swzhao.project.workflow.common.engine.OmpFlowEngine;
import top.swzhao.project.workflow.common.exception.ErrorCodes;
import top.swzhao.project.workflow.common.exception.OmpFlowException;
import top.swzhao.project.workflow.common.exception.process.ProcessCreateException;
import top.swzhao.project.workflow.common.exception.template.TemplateNotFoundException;
import top.swzhao.project.workflow.common.handler.FlowHandler;
import top.swzhao.project.workflow.common.model.bo.GlobalFlowParam;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.bo.TempFlowParam;
import top.swzhao.project.workflow.common.model.dto.FlowProcessSubProcessDto;
import top.swzhao.project.workflow.common.model.dto.FlowSubTplDto;
import top.swzhao.project.workflow.common.model.po.FlowSubTpl;
import top.swzhao.project.workflow.common.model.po.FlowTpl;
import top.swzhao.project.workflow.common.service.*;
import top.swzhao.project.workflow.core.utils.SpringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @Transactional(rollbackFor = Exception.class, transactionManager = FlowKvConstants.STR_KEY_TRANSACTION_MANAGER_FLOW)
    public OperResult start(String tplId, Map<String, Object> variables) throws OmpFlowException {
//        try {
//            HashMap<String, Object> globalParamMap = new HashMap<>(16);
//            OperResult<FlowTpl> tplOperResult = flowTplService.getTplById(tplId);
//            if (!tplOperResult.success() || Objects.isNull(tplOperResult.getData())) {
//                log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 获取模板失败， 入参:tplId:{}, 返回值: tplOperResult:{}"), tplId, tplOperResult);
//                throw new TemplateNotFoundException(ErrorCodes.TEMPLATE_ERROR, "获取模板失败");
//            }
//            FlowTpl flowTpl = tplOperResult.getData();
//            // 检查子任务是否存在，子任务顺序是否正常，最后返回已经排序号的子任务
//            List<FlowSubTplDto> flowSubTplDtoList = checkSubTpl(flowTpl);
//            log.info("实例化流程中......");
//            // 流程容器，入库后存储在dto中
//            FlowProcessSubProcessDto flowProcessSubProcessDto = new FlowProcessSubProcessDto();
//            dealProcessAndSubProcess(flowTpl, flowSubTplDtoList, flowProcessSubProcessDto);
//            log.info("实例化流程成功");
//            // 准备全局变量
//            GlobalFlowParam<Object> globalFlowParam = new GlobalFlowParam<>();
//            // 准备临时变量
//            TempFlowParam tempFlowParam = new TempFlowParam();
//            globalParamMap.put(FlowKvConstants.STR_KEY_FLOW_TPL, flowTpl);
//            globalParamMap.put(FlowKvConstants.STR_KEY_SUB_TPL_LIST, flowSubTplDtoList);
//            globalParamMap.put(FlowKvConstants.STR_KEY_PROCESS_SUB_PROCESS_DTO, flowProcessSubProcessDto);
//            // 初始化主线程，提交
//            // 提交线程之前提交一次事务，放置线程异步调用拿不到数据
//
//        } catch (ProcessCreateException pce) {
//            // 实例化流程失败，需要回滚删除所有的记录
//        } catch (OmpFlowException oe) {
//
//        }catch (Exception e) {
//
//        }

        return null;
    }




    @Override
    public OperResult reStart(String processId, Map<String, Object> variables) throws OmpFlowException {
        return null;
    }

    @Override
    public OperResult reRun(String processId, Map<String, Object> variables) throws OmpFlowException {
        return null;
    }

    @Override
    public OperResult skipCurrentAndRun(String processId, Map<String, Object> variables) throws OmpFlowException {
        return null;
    }

    @Override
    public OperResult rollback(String processId, Map<String, Object> variables) throws OmpFlowException {
        return null;
    }

    @Override
    public OperResult rollbackSubProcess(String processId, Map<String, Object> variables) throws OmpFlowException {
        return null;
    }



    private List<FlowSubTplDto> checkSubTpl(FlowTpl flowTpl) {
        return null;
    }


    private void dealProcessAndSubProcess(FlowTpl flowTpl, List<FlowSubTplDto> flowSubTplDtoList, FlowProcessSubProcessDto flowProcessSubProcessDto) {

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
