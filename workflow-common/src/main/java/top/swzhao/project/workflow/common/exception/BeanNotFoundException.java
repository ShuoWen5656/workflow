package top.swzhao.project.workflow.common.exception;

import top.swzhao.project.workflow.common.model.dto.FlowSubProcessSortDto;
import top.swzhao.project.workflow.common.model.po.FlowProcess;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/10/19 10:30 下午
 * @Discreption <>
 */
public class BeanNotFoundException extends EngineException{

    public BeanNotFoundException(Integer errorCode) {
        super(errorCode);
    }

    public BeanNotFoundException(Integer errorCode, String message) {
        super(errorCode, message);
    }
}
