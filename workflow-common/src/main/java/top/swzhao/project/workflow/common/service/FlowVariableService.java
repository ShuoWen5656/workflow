package top.swzhao.project.workflow.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.po.FlowVariable;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/11/6 9:49 下午
 * @Discreption <>
 */
public interface FlowVariableService extends IService<FlowVariable> {

    /**
     * 批量新增，这边建议分批次进行，否则会出现超市状况
     * @param flowVariableList
     * @return
     */
    OperResult batchCreate(List<FlowVariable> flowVariableList);

    /**
     * 批量查询符合条件的condition
     * @param condition
     * @return
     */
    OperResult<List<FlowVariable>> batchListByCondition(FlowVariable condition);


}
