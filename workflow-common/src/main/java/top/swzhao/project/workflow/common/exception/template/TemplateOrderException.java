package top.swzhao.project.workflow.common.exception.template;

/**
 * @author swzhao
 * @date 2023/10/20 9:27 下午
 * @Discreption <>
 */
public class TemplateOrderException extends TemplateException {
    public TemplateOrderException(Integer errorCode) {
        super(errorCode);
    }

    public TemplateOrderException(Integer errorCode, String message) {
        super(errorCode, message);
    }
}
