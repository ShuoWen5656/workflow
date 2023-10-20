package top.swzhao.project.workflow.common.exception.template;

import top.swzhao.project.workflow.common.exception.OmpFlowException;

/**
 * @author swzhao
 * @date 2023/10/20 9:25 下午
 * @Discreption <> 模板类型异常，其他模板具体异常继承该类
 */
public class TemplateException extends OmpFlowException {
    public TemplateException(Integer errorCode) {
        super(errorCode);
    }

    public TemplateException(Integer errorCode, String message) {
        super(errorCode, message);
    }
}
