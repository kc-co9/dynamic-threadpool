package com.share.co.kcl.dtp.monitor.security.annotation;

import java.lang.annotation.*;

/**
 * 用于校验接口是否有签名
 *
 * @author kcl.co
 * @since 2022/02/19
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Sign {
}
