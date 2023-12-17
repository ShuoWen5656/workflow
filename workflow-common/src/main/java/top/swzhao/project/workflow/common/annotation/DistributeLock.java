package top.swzhao.project.workflow.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author swzhao
 * @date 2023/12/17 12:49 下午
 * @Discreption <> 分布式锁注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DistributeLock {

    /**
     * 临界区标识，支持SPEL表达式
     * @return
     */
    String key() default "";

    /**
     * 锁过期时间（s）
     * @return
     */
    int timeout() default 10;

    /**
     * 是否是互斥场景
     * true：取号器场景，等待自旋
     * false：互斥场景，执行后不再执行
     * @return
     */
    boolean loopWithLockFail() default false;

    /**
     * 自旋尝试次数，默认一百次
     * 一次等待100ms
     * @return
     */
    int tryTime() default 100;


}
