package top.swzhao.project.workflow.common.exception.process;

import top.swzhao.project.workflow.common.exception.OmpFlowException;

/**
 * @author swzhao
 * @date 2023/10/20 9:21 下午
 * @Discreption <> 流程公共异常
 */
public class ProcessException extends OmpFlowException {


    public ProcessException(Integer errorCode) {
        super(errorCode);
    }

    public ProcessException(Integer errorCode, String message) {
        super(errorCode, message);
    }
}
