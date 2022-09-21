package com.share.co.kcl.threadpool.monitor.annotation;

import com.share.co.kcl.threadpool.monitor.processor.aop.IgnoreErrAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于在方法级别上忽略异常，具体实现可阅读{@link IgnoreErrAspect}
 *
 * @author K.C
 * @since 2022-08-09
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface IgnoreErr {

    /**
     * 指定忽略异常类型，默认为{@link Exception}
     *
     * @return 指定异常Class类型
     */
    Class<? extends Throwable> specifiedError() default Exception.class;

    /**
     * 发生异常时返回的结果，默认返回null
     * <p>
     * note：
     * 1. 对于基本类型需要使用其包装类
     * 2. 如果方法返回类型是基本类型，则会发生不可预知的错误
     *
     * @return SpEL表达式
     */
    String errorResult() default "";

    /**
     * 是否打印异常信息，默认打印错误日志
     * <p>
     * note:
     * - 如果指定异常匹配失败则不会打印错误日志
     *
     * @return 是否打印异常信息
     */
    boolean isPrintError() default true;
}
