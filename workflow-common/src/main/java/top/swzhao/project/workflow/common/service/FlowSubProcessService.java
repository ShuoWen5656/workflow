package top.swzhao.project.workflow.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.po.FlowSubProcess;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/11/6 9:36 下午
 * @Discreption <>
 */
public interface FlowSubProcessService extends IService<FlowSubProcess> {

    /**
     * 批量创建子流程
     * @param flowSubProcesses
     * @return
     */
    OperResult batchCreate(List<? extends FlowSubProcess> flowSubProcesses);

    /**
     * 根据所属processId批量
     * @param processId
     * @return
     */
    OperResult deleteByProcessId(String processId);

    /**
     * 批量更新子任务状态
     * @param flowSubProcesses
     * @return
     */
    OperResult batchUpdate(List<FlowSubProcess> flowSubProcesses);

    /**
     * 根据条件查询子流程列表
     * @param condition
     * @param order
     * @return
     */
    OperResult<List<FlowSubProcess>> listSubProcessByCondition(FlowSubProcess condition, String order);


}
