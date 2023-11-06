package top.swzhao.project.workflow.common.service;

import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.dto.FlowSubTplDto;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/11/6 9:46 下午
 * @Discreption <>
 */
public interface FlowTplSubClassNameService {

    /**
     * 根据模板id获取模板对应的子模板列表
     * @param tplId
     * @return
     */
    OperResult<List<FlowSubTplDto>> listFlowSubTplByTplId(String tplId);


}
