package top.swzhao.project.workflow.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author swzhao
 * @data 2023/9/10 15:09
 * @Discreption <> 注解：子任务描述，用于入库时信息录入
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FlowDescription {

    /**
     * 子任务描述
     * @return
     */
    String description() default "";

    /**
     * 子任务类型：保留字段
     * @return
     */
    int type() default 0;

}
