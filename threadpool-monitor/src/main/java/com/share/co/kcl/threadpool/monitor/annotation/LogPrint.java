package com.share.co.kcl.threadpool.monitor.annotation;

import com.share.co.kcl.threadpool.monitor.processor.aop.LogAspect;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.*;

/**
 * 可用于日志指定字段打印，如果不选或者不加注解全部打印，即默认全部打印。
 * 逻辑实现可看{@link LogAspect}
 *
 * @author KIMVON
 * @since 2021/9/9
 */
@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogPrint {

    LogField[] field() default {LogField.ORIGIN_HEADER, LogField.ORIGIN_BODY, LogField.CLASS, LogField.METHOD, LogField.METHOD_PARAMETER, LogField.METHOD_RETURN, LogField.EXCEPTION, LogField.DURATION};

    @Getter
    @AllArgsConstructor
    enum LogField {
        /**
         * log for original request header
         */
        ORIGIN_HEADER("请求原始header"),

        /**
         * log for original request body
         */
        ORIGIN_BODY("请求原始body"),

        /**
         * log for class
         */
        CLASS("请求类"),

        /**
         * log for method
         */
        METHOD("请求方法"),

        /**
         * log for method parameter
         */
        METHOD_PARAMETER("请求方法参数"),

        /**
         * log for method return val
         */
        METHOD_RETURN("请求方法返回"),

        /**
         * log for exception
         */
        EXCEPTION("异常情况"),

        /**
         * log for duration
         */
        DURATION("响应时间"),
        ;

        final String msg;

        public static LogField[] defaultPrint() {
            return LogField.values();
        }
    }

}
