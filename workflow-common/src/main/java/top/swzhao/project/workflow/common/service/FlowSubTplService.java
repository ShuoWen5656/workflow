package top.swzhao.project.workflow.common.service;

import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.po.FlowSubTpl;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/11/6 9:43 下午
 * @Discreption <>
 */
public interface FlowSubTplService {

    /**
     * 插入或更新sub_tpl表，用于初始化引擎使用
     * @param flowSubTpls
     * @return
     */
    OperResult insertOrUpdate(List<FlowSubTpl> flowSubTpls);
}
