package com.share.co.kcl.dtp.spring;

import com.share.co.kcl.dtp.spring.processor.DynamicPoolConfigSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * enable the dynamic thread pool
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DynamicPoolConfigSelector.class})
public @interface EnableDynamicPool {
}
