package top.swzhao.project.workflow.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author swzhao
 * @date 2023/12/17 12:12 下午
 * @Discreption <> 处理rediskey缓存的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface RedisToolAnnotation {

    /**
     * 处理的keys列表
     *
     * @return
     */
    String[] keys() default "";


    /**
     * 对key的操作：默认为删除
     *
     * @return
     */
    String opt() default "del";

    /**
     * dbIndex
     *
     * @return
     */
    int dbIndex() default 0;


}
