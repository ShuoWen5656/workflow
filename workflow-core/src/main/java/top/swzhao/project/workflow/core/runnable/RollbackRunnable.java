package top.swzhao.project.workflow.core.runnable;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import top.swzhao.project.workflow.common.enums.ProcessStateEnum;
import top.swzhao.project.workflow.common.exception.BeanNotFoundException;
import top.swzhao.project.workflow.common.exception.process.ProcessModificateException;
import top.swzhao.project.workflow.common.exception.variable.VariableCreateException;
import top.swzhao.project.workflow.common.model.bo.GlobalFlowParam;
import top.swzhao.project.workflow.common.model.bo.TempFlowParam;
import top.swzhao.project.workflow.common.model.dto.FlowSubProcessSortDto;
import top.swzhao.project.workflow.common.model.po.FlowProcess;
import top.swzhao.project.workflow.core.engineimpl.OmpFlowEngineImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author swzhao
 * @date 2023/12/13 8:52 下午
 * @Discreption <>
 */
@Slf4j
public class RollbackRunnable extends BaseRunnable{


    /**
     * 初始化
     * @param globalFlowParam
     * @param tempFlowParam
     * @param ompFlowEngine
     * @param action
     */
    public RollbackRunnable(GlobalFlowParam globalFlowParam, TempFlowParam tempFlowParam, OmpFlowEngineImpl ompFlowEngine, String action) {
        init(globalFlowParam, tempFlowParam, ompFlowEngine, action);
    }

    /**
     * 1、取流程，按照顺序执行rollback流程
     */
    @Override
    public void run() {
        try {
            // 获取子流程列表
            List<FlowSubProcessSortDto> flowSubProcessSortDtoList = this.flowProcessSubProcessDto.getFlowSubProcessSortDtoList();
            // 按照顺序执行，因为flowSubTplDto已经有序，这里使用链表实现的hash可以保证顺序不变
            LinkedHashMap<String, List<FlowSubProcessSortDto>> processSortMap = flowSubProcessSortDtoList.stream().collect(Collectors.groupingBy(FlowSubProcessSortDto::getSort, LinkedHashMap::new, Collectors.toList()));
            // 按照顺序执行
            for (String sort : processSortMap.keySet()) {
                // 执行列表，正常来说这里列表长度为1，如果模板配置为12333，则这里3的顺序就会存在三个子任务，同时执行
                List<FlowSubProcessSortDto> flowSubProcessSortDtos = processSortMap.get(sort);
                // 获取流程列表
                if (CollectionUtils.isEmpty(flowSubProcessSortDtos)) {
                    continue;
                }
                // 执行前准备
                beforeExecuteForRollback(flowSubProcessSortDtos);
                execute(flowSubProcessSortDtos);
                /**
                 * 执行后收尾
                 * 1、检查当前子任务是否全部成功，并将全部子任务状态和错误（若有）入库
                 * 2、如果存在子任务失败状态，刷新process更改为失败状态并结束进程
                 * 3、如果子任务全部成功，则全局变量不变，局部变量input和output调换位置，继续下一个顺序执行
                 */
                if(!afterExecuteForRollback(flowSubProcessSortDtos)) {
                    return;
                }
            }
            // 能走到这里说明全部都成功了
            FlowProcess flowProcess = this.flowProcessSubProcessDto.getFlowProcess();
            flowProcess.setState(ProcessStateEnum.PROCESS_STATE_ROLLBACK_FINISH.getCode().toString());
            ompFlowEngine.getFlowHandler().dealProcess(new ArrayList<>(), flowProcess);
        } catch (ProcessModificateException | VariableCreateException | BeanNotFoundException pme) {
          // 更新子流程失败，这种为引擎内部失败，到这里不应该再删除流程，应该更改流程的状态和错误节点并结束主线程
          // 创建变量失败，应该更改流程状态和错误节点并结束主线程
          dealEngineException(pme);
        } catch (Exception e) {
            log.error("回滚异常，原因：{}", e.getMessage(), e);
        }
    }
}
