package top.swzhao.substeps;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.swzhao.project.workflow.common.OmpFlowable;
import top.swzhao.project.workflow.common.annotation.FlowDescription;
import top.swzhao.project.workflow.common.model.bo.GlobalFlowParam;
import top.swzhao.project.workflow.common.model.bo.TempFlowParam;

/**
 * @author swzhao
 * @date 2024/7/27 10:33 上午
 * @Discreption <>
 */
@FlowDescription(description = "测试步骤1")
@Slf4j
@Service
public class Step1 implements OmpFlowable {


    @Override
    public void start(TempFlowParam tempFlowParam, GlobalFlowParam globalFlowParam) {
        log.warn("flowable Step1 start！");
    }

    @Override
    public void rollback(TempFlowParam tempFlowParam, GlobalFlowParam globalFlowParam) {
        log.warn("flowable Step1 rollback！");
    }
}
