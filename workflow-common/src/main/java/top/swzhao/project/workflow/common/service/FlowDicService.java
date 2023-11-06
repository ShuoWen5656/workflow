package top.swzhao.project.workflow.common.service;

import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.po.FlowDic;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/11/6 9:33 下午
 * @Discreption <>
 */
public interface FlowDicService {


    /**
     * 查询某种类型的dic
     * @param type
     * @return
     */
    OperResult<List<FlowDic>> listDicByType(String type);
}
