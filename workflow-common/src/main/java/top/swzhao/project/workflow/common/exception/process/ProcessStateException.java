package top.swzhao.project.workflow.common.exception.process;

import top.swzhao.project.workflow.common.exception.OmpFlowException;

/**
 * @author swzhao
 * @date 2023/10/20 9:23 下午
 * @Discreption <>
 */
public class ProcessStateException extends OmpFlowException {
    public ProcessStateException(Integer errorCode) {
        super(errorCode);
    }

    public ProcessStateException(Integer errorCode, String message) {
        super(errorCode, message);
    }
}
