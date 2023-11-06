package top.swzhao.project.workflow.common.service;

import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.po.FlowProcess;

/**
 * @author swzhao
 * @date 2023/11/6 9:34 下午
 * @Discreption <>
 */
public interface FlowProcessService {

    /**
     * 根据id获取记录
     * @param id
     * @return
     */
    OperResult<FlowProcess> getById(String id);

    /**
     * 创建flowProcess
     * @param flowProcess
     * @return
     */
    OperResult createProcess(FlowProcess flowProcess);

    /**
     * 根据id删除一条记录
     * @param processId
     * @return
     */
    OperResult deleteById(String processId);

    /**
     * 更新
     * 1、id必填
     * 2、null值会被过滤
     * @param flowProcess
     * @return
     */
    OperResult update(FlowProcess flowProcess);


}
