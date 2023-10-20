package top.swzhao.project.workflow.common.exception.template;

/**
 * @author swzhao
 * @date 2023/10/20 9:26 下午
 * @Discreption <>
 */
public class TemplateNotFoundException extends TemplateException {
    public TemplateNotFoundException(Integer errorCode) {
        super(errorCode);
    }

    public TemplateNotFoundException(Integer errorCode, String message) {
        super(errorCode, message);
    }
}
