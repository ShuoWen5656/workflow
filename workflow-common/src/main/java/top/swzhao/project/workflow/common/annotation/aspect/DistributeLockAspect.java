package top.swzhao.project.workflow.common.annotation.aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import top.swzhao.project.workflow.common.annotation.DistributeLock;
import top.swzhao.project.workflow.common.utils.RedisUtil;

import java.util.Objects;
import java.util.UUID;

/**
 * @author swzhao
 * @date 2023/12/17 1:10 下午
 * @Discreption <>
 */
@Aspect
@Component
@Slf4j
public class DistributeLockAspect {

    /**
     * 当前线程标识UUID
     */
    private static final ThreadLocal<String> uniqueIdThreadLocal = new ThreadLocal<>();


    @Pointcut(value = "@annotation(top.swzhao.project.workflow.common.annotation.DistributeLock)")
    public void scheduleLock(){

    }


    @Around(value = "scheduleLock() && @annotation(lock)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, DistributeLock lock) {
        // 解析当前需要加锁的业务标识
        String key = generateKeyBySpEL(lock.key(), proceedingJoinPoint);
        // 锁过期的时间
        int timeout = lock.timeout();
        // 是否自旋
        boolean isLoop = lock.loopWithLockFail();
        // 自选尝试次数
        int loopTime = lock.tryTime();
        boolean setResult = false;
        try {
            int count = 0;
            // 设置当前线程的标识
            uniqueIdThreadLocal.set(UUID.randomUUID().toString());
            // 先尝试一次
            setResult = RedisUtil.setRedisLock(key, uniqueIdThreadLocal.get(), timeout);
            if (!setResult) {
                // 没成功则根据策略进行处理
                if (!isLoop) {
                    // 互斥场景
                    return null;
                }
                // 取号器场景
                while (!setResult && count++ < loopTime) {
                    setResult = RedisUtil.setRedisLock(key, uniqueIdThreadLocal.get(), timeout);
                    Thread.sleep(100);
                }
                // 结束后如果还是没获取到则直接异常退出
                if (!setResult) {
                    log.warn("自旋获取锁超时，请查看lock :{} 是否未被释放", key);
                    throw new Exception("自旋获取锁超时");
                }
            }
            return proceedingJoinPoint.proceed();
        } catch (Exception e) {
            // 这里是锁异常，正常业务异常需要提前捕获并抛出
            log.error("分布式锁加锁异常:", e);
            return null;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            // 释放分布式锁
            if (setResult) {
                String uuid = uniqueIdThreadLocal.get();
                uniqueIdThreadLocal.remove();
                // 仅释放当前线程加的锁，不要释放别人的锁
                RedisUtil.luaExistKeyDel(key, uuid);
            }
        }
    }

    private SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    private DefaultParameterNameDiscoverer defaultParameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 将入参替换到key中的占位符
     * @param key
     * @param proceedingJoinPoint
     * @return
     */
    private String generateKeyBySpEL(String key, ProceedingJoinPoint proceedingJoinPoint) {
        // 转换表达式
        Expression expression = spelExpressionParser.parseExpression(key);
        // 获取上下文
        EvaluationContext context = new StandardEvaluationContext();
        // 从当前方法中获取用户输入的参数
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        // 注解方法中的入参
        Object[] args = proceedingJoinPoint.getArgs();
        // 从方法中获取未被压缩过的参数名：正常来说压缩过的为arg1，arg2，这样找不到用户的真实参数名称
        String[] parameterNames = defaultParameterNameDiscoverer.getParameterNames(signature.getMethod());
        if (parameterNames == null || parameterNames.length == 0) {
            return key;
        }
        // 判断是否存在spel表达式
        boolean flag = false;
        for (int i = 0; i < parameterNames.length; i++) {
            if (StringUtils.isNotBlank(parameterNames[i]) && key.contains(parameterNames[i])) {
                flag = true;
                context.setVariable(parameterNames[i], args);
            }
        }
        return !flag ? key : Objects.requireNonNull(expression.getValue(context).toString());
    }


}
