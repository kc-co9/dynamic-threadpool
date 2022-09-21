package com.share.co.kcl.threadpool.monitor.processor.aop;

import com.share.co.kcl.common.exception.ToastException;
import com.share.co.kcl.threadpool.monitor.annotation.Lock;
import com.share.co.kcl.threadpool.monitor.sdk.helper.LockHelper;
import com.share.co.kcl.threadpool.monitor.utils.AopUtils;
import com.share.co.kcl.threadpool.monitor.utils.SpELUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author KIMVON
 * @since 2021/7/19
 */
@Aspect
@Component
public class LockAspect {

    private static final String LOCK_ASPECT_PREFIX = "lock_aspect";

    @Pointcut("@annotation(com.share.co.kcl.threadpool.monitor.annotation.Lock)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取查询的key 连接点是在方法上的,所以可以强转成方法的签名信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取被注解注释的方法
        Method method = AopUtils.getMethod(joinPoint);

        Lock lock = method.getAnnotation(Lock.class);
        EvaluationContext context = SpELUtils.createContext(signature.getParameterNames(), joinPoint.getArgs());

        String key = SpELUtils.parseSpEL(lock.key(), context);
        if (lock.prefix()) {
            key = String.format("%s:%s:%s:%s", LOCK_ASPECT_PREFIX, method.getDeclaringClass().getName(), method.getName(), key);
        }

        boolean success = LockHelper.tryLock(key, lock.timeout(), Math.max(lock.waittime(), 0));
        if (!success) {
            throw new ToastException("操作太频繁，请稍后重试");
        }
        try {
            return joinPoint.proceed();
        } finally {
            LockHelper.releaseLock(key);
        }
    }

}
