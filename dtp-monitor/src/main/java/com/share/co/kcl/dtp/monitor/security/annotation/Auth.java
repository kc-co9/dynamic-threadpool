package com.share.co.kcl.dtp.monitor.security.annotation;

import java.lang.annotation.*;

/**
 * 用于校验用户是否有登录
 *
 * @author kcl.co
 * @since 2022/02/19
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Auth {
}
