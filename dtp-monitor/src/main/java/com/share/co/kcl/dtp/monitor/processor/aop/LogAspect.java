package com.share.co.kcl.dtp.monitor.processor.aop;

import com.alibaba.fastjson2.JSON;
import com.google.common.base.Throwables;
import com.share.co.kcl.dtp.monitor.annotation.LogPrint;
import com.share.co.kcl.dtp.monitor.utils.AopUtils;
import com.share.co.kcl.dtp.monitor.utils.NetworkUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.share.co.kcl.dtp.monitor.annotation.LogPrint.LogField.*;


/**
 * 日志AOP，需要实现子类，并实现相应得方法{@link LogAspect#pointcut()}
 * <p>
 * 如需指定特定输出字段，可使用注解{@link LogPrint}
 *
 * @author K.C
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    /**
     * 记录请求时间用于计算接口响应时间
     */
    private static final ThreadLocal<Long> VISIT_TIME = new ThreadLocal<>();
    private static final ThreadLocal<Object> RETURN_VAL = new ThreadLocal<>();
    private static final ThreadLocal<Throwable> RETURN_ERROR = new ThreadLocal<>();

    /**
     * 需要修改包路径
     */
    @Pointcut(value = "execution(* com.share.co.kcl.threadpool..*.controller..*.*(..))")
    protected void pointcut() {
    }

    @Before(value = "pointcut()")
    public void doBefore() {
        VISIT_TIME.set(System.currentTimeMillis());
    }

    @AfterReturning(value = "pointcut()", returning = "val")
    public void doAfterReturning(Object val) {
        RETURN_VAL.set(val);
    }

    @AfterThrowing(value = "pointcut()", throwing = "e")
    public void doAfterThrowing(Throwable e) {
        RETURN_ERROR.set(e);
    }

    /**
     * 打印日志
     */
    @After(value = "pointcut()")
    public void doAfter(JoinPoint point) {
        try {
            ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Map<String, String> header = Optional.ofNullable(sra)
                    .map(ServletRequestAttributes::getRequest)
                    .map(NetworkUtils::getHeader)
                    .orElse(Collections.emptyMap());
            Map<String, Object> body = Optional.ofNullable(sra)
                    .map(ServletRequestAttributes::getRequest)
                    .map(NetworkUtils::getMultiBody)
                    .orElse(Collections.emptyMap());

            Class<?> clazz = AopUtils.getClass(point);
            Method method = AopUtils.getMethod(point);
            Object[] params = AopUtils.getAvailableArgs(point.getArgs());

            Object returnVal = RETURN_VAL.get();
            Throwable error = RETURN_ERROR.get();
            long duration = System.currentTimeMillis() - VISIT_TIME.get();

            LogPrint.LogField[] fields =
                    Optional.ofNullable(method.getAnnotation(LogPrint.class))
                            .map(LogPrint::field)
                            .orElse(LogPrint.LogField.defaultPrint());
            Set<LogPrint.LogField> fieldSet = Arrays.stream(fields).collect(Collectors.toSet());

            StringBuilder info = new StringBuilder()
                    .append(fieldSet.contains(ORIGIN_HEADER) ? printLine(ORIGIN_HEADER, JSON.toJSONString(header)) : "")
                    .append(fieldSet.contains(ORIGIN_BODY) ? printLine(ORIGIN_BODY, JSON.toJSONString(body)) : "")
                    .append(fieldSet.contains(CLASS) ? printLine(CLASS, clazz.getName()) : "")
                    .append(fieldSet.contains(METHOD) ? printLine(METHOD, method.getName()) : "")
                    .append(fieldSet.contains(METHOD_PARAMETER) ? printLine(METHOD_PARAMETER, JSON.toJSONString(params)) : "")
                    .append(fieldSet.contains(METHOD_RETURN) ? printLine(METHOD_RETURN, JSON.toJSONString(returnVal)) : "")
                    .append(fieldSet.contains(EXCEPTION) ? printLine(EXCEPTION, Optional.ofNullable(error).map(Throwables::getStackTraceAsString).orElse("")) : "")
                    .append(fieldSet.contains(DURATION) ? printLine(DURATION, String.format("%s毫秒", duration)) : "");
            LOGGER.info("LogAspect print request log: {}", info);
        } catch (Exception e) {
            LOGGER.error("LogAspect aspect invoke failure", e);
        } finally {
            VISIT_TIME.remove();
            RETURN_VAL.remove();
            RETURN_ERROR.remove();
        }
    }

    private StringBuilder printLine(LogPrint.LogField field, String value) {
        return new StringBuilder()
                .append(field.getMsg())
                .append(":")
                .append(value)
                .append(System.lineSeparator());
    }
}
