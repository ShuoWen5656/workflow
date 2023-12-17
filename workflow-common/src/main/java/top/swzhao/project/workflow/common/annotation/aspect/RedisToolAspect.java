package top.swzhao.project.workflow.common.annotation.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import top.swzhao.project.workflow.common.annotation.RedisToolAnnotation;
import top.swzhao.project.workflow.common.utils.RedisUtil;

import java.util.Map;

/**
 * @author swzhao
 * @date 2023/12/17 12:20 下午
 * @Discreption <> 处理rediskey的前面
 * {@link top.swzhao.project.workflow.common.annotation.RedisToolAnnotation}
 */
@Aspect
@Component
@Slf4j
public class RedisToolAspect {

    /**
     * 被注解的方法可以通过设置threadlocal的方式来进行额外的操作
     */
    public static final ThreadLocal<Map<String, String>> dealKV = new ThreadLocal<>();



    @AfterReturning("@annotation(RedisToolAnnotation)")
    public void doAfter(JoinPoint joinpoint) {
        try {
            MethodSignature signature = (MethodSignature) joinpoint.getSignature();
            RedisToolAnnotation annotation = signature.getMethod().getAnnotation(RedisToolAnnotation.class);
            String[] keys = annotation.keys();
            String opt = annotation.opt();
            int dbIndex = annotation.dbIndex();
            // 仅有删除功能，后面可自行拓展
            switch (opt) {
                case "del" :
                    RedisUtil.delList(keys, dbIndex);
                default:
                    break;
            }
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常， 原因：{}"), e.getMessage(), e);
        }
    }



}
