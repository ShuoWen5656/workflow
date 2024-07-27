package top.swzhao.substeps;

import lombok.extern.slf4j.Slf4j;
import top.swzhao.project.workflow.common.OmpFlowable;
import top.swzhao.project.workflow.common.annotation.FlowDescription;
import top.swzhao.project.workflow.common.model.bo.GlobalFlowParam;
import top.swzhao.project.workflow.common.model.bo.TempFlowParam;

/**
 * @author swzhao
 * @date 2024/7/27 10:34 上午
 * @Discreption <>
 */
@FlowDescription(description = "执行步骤2")
@Slf4j
public class Step2 implements OmpFlowable {
    @Override
    public void start(TempFlowParam tempFlowParam, GlobalFlowParam globalFlowParam) {
        log.warn("flowable Step2 start！");
    }

    @Override
    public void rollback(TempFlowParam tempFlowParam, GlobalFlowParam globalFlowParam) {
        log.warn("flowable Step2 rollback！");
    }
}
