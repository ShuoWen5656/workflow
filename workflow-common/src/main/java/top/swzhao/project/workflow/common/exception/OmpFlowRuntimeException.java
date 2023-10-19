package top.swzhao.project.workflow.common.exception;

/**
 * @author swzhao
 * @date 2023/10/19 10:24 下午
 * @Discreption <>
 */
public class OmpFlowRuntimeException extends RuntimeException {

    private Integer errorCode;

    /**
     * 递归异常堆栈
     */
    private Throwable e;


    public OmpFlowRuntimeException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public OmpFlowRuntimeException(Integer errorCode) {
        super(ErrorCodes.convert2Msg(errorCode));
        this.errorCode = errorCode;
    }

    public OmpFlowRuntimeException(Integer errorCode, Throwable e) {
        super(ErrorCodes.convert2Msg(errorCode), e);
        this.errorCode = errorCode;
        this.e = e;
    }

    public OmpFlowRuntimeException(String message, Integer errorCode, Throwable e) {
        super(message, e);
        this.errorCode = errorCode;
        this.e = e;
    }
}
