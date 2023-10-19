package top.swzhao.project.workflow.common.exception;

/**
 * @author swzhao
 * @date 2023/10/19 10:19 下午
 * @Discreption <> 受检异常：需要客户端处理此类异常，比如模板不存在时客户端需要自行处理这类异常
 */
public class OmpFlowException extends Exception {

    private Integer errorCode;

    public OmpFlowException(Integer errorCode) {
        super(ErrorCodes.convert2Msg(errorCode));
        this.errorCode = errorCode;
    }

    public OmpFlowException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
