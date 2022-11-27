package com.share.co.kcl.dtp.monitor.security.annotation;

import java.lang.annotation.*;

/**
 * 用于校验用户权限
 *
 * @author kcl.co
 * @since 2022/02/19
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Permission {
    /**
     * Returns the list of security configuration attributes (e.g.&nbsp;permission1, permission2).
     *
     * @return String[] The secure method attributes
     */
    String[] value();
}
