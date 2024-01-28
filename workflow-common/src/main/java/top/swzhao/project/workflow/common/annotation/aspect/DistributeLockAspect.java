package top.swzhao.project.workflow.common.annotation.aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.concurrent.TimeUnit;

/**
 * @author swzhao
 * @date 2023/12/17 1:10 下午
 * @Discreption <>
 */
@Aspect
@Component
@Slf4j
public class DistributeLockAspect {


    @Autowired
    private RedissonClient redissonClient;

    @Pointcut(value = "@annotation(top.swzhao.project.workflow.common.annotation.DistributeLock)")
    public void scheduleLock(){

    }


    @Around(value = "scheduleLock() && @annotation(lock)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, DistributeLock lock) throws Throwable {
        // 解析当前需要加锁的业务标识
        String key = generateKeyBySpEL(lock.key(), proceedingJoinPoint);
        // 锁过期的时间
        int timeout = lock.timeout();
        // 是否自旋
        boolean isLoop = lock.loopWithLockFail();
        // 自选尝试次数
        int loopTime = lock.tryTime();
        boolean setResult = false;
        RLock rLock = redissonClient.getLock(key);
        try {
            if (rLock.isLocked() && !isLoop) {
                // 不再尝试直接退出
                log.info("当前线程没有获取到分布式锁[key:{}]，锁已被其他线程占用，退出...", key);
                return null;
            }
            // 到这里要么是场景没有锁，要么就是需要自旋获取锁
            boolean success = rLock.tryLock(loopTime, timeout, TimeUnit.SECONDS);
            if (!success) {
                log.warn("分布式锁获取失败！请检查key:{} 是否长时间未释放！", key);
                throw new Exception("分布式锁抢占失败");
            }
            return proceedingJoinPoint.proceed();
        } catch (Exception e) {
            // 这里是锁异常，正常业务异常需要提前捕获并抛出
            log.error("分布式锁加锁异常:", e);
            throw e;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        } finally {
            // 释放锁
            if (Objects.nonNull(rLock) && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
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
