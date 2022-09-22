package com.share.co.kcl.dtp.monitor.processor.aop;

import com.share.co.kcl.dtp.monitor.annotation.Cache;
import com.share.co.kcl.dtp.monitor.sdk.helper.CacheHelper;
import com.share.co.kcl.dtp.monitor.utils.AopUtils;
import com.share.co.kcl.dtp.monitor.utils.SpELUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author KIMVON
 * @since 2021/7/20
 */
@Aspect
@Component
public class CacheAspect {
    private static final String CACHE_ASPECT_PREFIX = "cache_aspect";

    @Pointcut("@annotation(com.share.co.kcl.dtp.monitor.annotation.Cache)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取查询的key 连接点是在方法上的,所以可以强转成方法的签名信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取被注解注释的方法
        Method method = AopUtils.getMethod(joinPoint);

        Cache cache = method.getAnnotation(Cache.class);
        EvaluationContext context = SpELUtils.createContext(signature.getParameterNames(), joinPoint.getArgs());

        String key = String.format("%s:%s:%s:%s", CACHE_ASPECT_PREFIX, method.getDeclaringClass().getName(),
                method.getName(), SpELUtils.parseSpEL(cache.key(), context));

        Object value = CacheHelper.get(key, method.getReturnType()).orElse(null);
        if (Objects.isNull(value)) {
            value = joinPoint.proceed();
            CacheHelper.set(key, value, cache.timeout(), TimeUnit.SECONDS);
        }
        return value;
    }
}
