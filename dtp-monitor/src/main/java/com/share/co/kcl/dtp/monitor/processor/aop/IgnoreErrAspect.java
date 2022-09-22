package com.share.co.kcl.dtp.monitor.processor.aop;

import com.share.co.kcl.dtp.common.utils.ReflectUtils;
import com.share.co.kcl.dtp.monitor.annotation.IgnoreErr;
import com.share.co.kcl.dtp.monitor.utils.AopUtils;
import com.share.co.kcl.dtp.monitor.utils.SpELUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class IgnoreErrAspect {

    private static final Logger LOG = LoggerFactory.getLogger(IgnoreErrAspect.class);

    @Pointcut("@annotation(com.share.co.kcl.dtp.monitor.annotation.IgnoreErr)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取查询的key 连接点是在方法上的,所以可以强转成方法的签名信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取被注解注释的方法
        Method method = AopUtils.getMethod(joinPoint);
        IgnoreErr ignoreErr = method.getAnnotation(IgnoreErr.class);

        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            if (!ignoreErr.specifiedError().isAssignableFrom(ex.getClass())) {
                throw ex;
            }
            if (ignoreErr.isPrintError()) {
                LOG.error("IgnoreErrAspect invoke failure", ex);
            }
            if (StringUtils.isBlank(ignoreErr.errorResult())) {
                return this.defaultErrorResponse(method);
            }
            try {
                EvaluationContext context = SpELUtils.createContext(signature.getParameterNames(), joinPoint.getArgs());
                return SpELUtils.parseSpEL(ignoreErr.errorResult(), context);
            } catch (Exception subEx) {
                LOG.error("IgnoreErrAspect parse SpEL failure", subEx);
                return this.defaultErrorResponse(method);
            }
        }
    }

    private Object defaultErrorResponse(Method method) {
        Class<?> returnType = method.getReturnType();
        if (ReflectUtils.isBaseType(returnType)) {
            if (int.class.equals(returnType)) {
                return 0;
            } else if (long.class.equals(returnType)) {
                return 0L;
            } else if (short.class.equals(returnType)) {
                return 0;
            } else if (boolean.class.equals(returnType)) {
                return false;
            } else if (float.class.equals(returnType)) {
                return 0f;
            } else if (double.class.equals(returnType)) {
                return 0d;
            } else if (byte.class.equals(returnType)) {
                return 0;
            }
        }
        return null;
    }
}
