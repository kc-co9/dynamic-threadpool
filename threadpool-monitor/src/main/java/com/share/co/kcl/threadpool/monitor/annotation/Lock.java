package com.share.co.kcl.threadpool.monitor.annotation;

import com.share.co.kcl.threadpool.monitor.processor.aop.LockAspect;

import java.lang.annotation.*;

/**
 * 通过AOP{@link LockAspect}实现注解功能，具体逻辑可以阅读相关代码。
 * 此处需要注意，依据AOP的实现原理，使用时需要通过Spring代理的方式（即通过依赖注入）来调用所修饰的方法才能生效。
 *
 * @author KIMVON
 * @since 2021/7/19
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {
    /**
     * 是否追加默认前缀
     */
    boolean prefix() default true;

    /**
     * 加锁key，默认格式是method-name:key key生成策略SpEL
     */
    String key() default "";

    /**
     * 执行超时释放时间，单位秒 必填，不允许没有过期时间
     */
    long timeout();

    /**
     * 等待锁的时间，单位秒 必填
     *
     * @return
     */
    long waittime() default 0L;
}
