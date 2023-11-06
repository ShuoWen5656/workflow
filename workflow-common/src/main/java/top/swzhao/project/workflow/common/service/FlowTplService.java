package top.swzhao.project.workflow.common.service;

import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.po.FlowTpl;

/**
 * @author swzhao
 * @date 2023/11/6 9:45 下午
 * @Discreption <> 任务流程相关
 */
public interface FlowTplService {

    /**
     * 根据id获取一个实体
     * @param id
     * @return
     */
    OperResult<FlowTpl> getTplById(String id);
}
