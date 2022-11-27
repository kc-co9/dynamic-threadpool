package com.share.co.kcl.dtp.monitor.security.authorization.metadatasource;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 重写 org.springframework.security.access.method.DelegatingMethodSecurityMetadataSource
 *
 * @author kcl.co
 * @since 2022/02/19
 */
public class DtpDelegatingMetadataSource extends AbstractMethodSecurityMetadataSource {
    private static final List<ConfigAttribute> NULL_CONFIG_ATTRIBUTE = Collections.emptyList();

    private final List<MethodSecurityMetadataSource> methodSecurityMetadataSources;
    private final Map<DefaultCacheKey, Collection<ConfigAttribute>> attributeCache = new HashMap<>();

    // ~ Constructor
    // ====================================================================================================

    public DtpDelegatingMetadataSource(List<MethodSecurityMetadataSource> methodSecurityMetadataSources) {
        Assert.notNull(methodSecurityMetadataSources, "MethodSecurityMetadataSources cannot be null");
        this.methodSecurityMetadataSources = methodSecurityMetadataSources;
    }

    // ~ Methods
    // ========================================================================================================

    public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
        DefaultCacheKey cacheKey = new DefaultCacheKey(method, targetClass);
        synchronized (attributeCache) {
            Collection<ConfigAttribute> cached = attributeCache.get(cacheKey);
            // Check for canonical value indicating there is no config attribute,

            if (cached != null) {
                return cached;
            }

            /**
             * No cached value, so query the sources to find a result
             * 修改此处，全部判断逻辑都要
             */
            Collection<ConfigAttribute> attributes = new ArrayList<>();
            for (MethodSecurityMetadataSource s : methodSecurityMetadataSources) {
                Collection<ConfigAttribute> singleAttributes = s.getAttributes(method, targetClass);
                if (!CollectionUtils.isEmpty(singleAttributes)) {
                    attributes.addAll(singleAttributes);
                }
            }

            // Put it in the cache.
            if (CollectionUtils.isEmpty(attributes)) {
                this.attributeCache.put(cacheKey, NULL_CONFIG_ATTRIBUTE);
                return NULL_CONFIG_ATTRIBUTE;
            }

            this.attributeCache.put(cacheKey, attributes);

            return attributes;
        }
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> set = new HashSet<>();
        for (MethodSecurityMetadataSource s : methodSecurityMetadataSources) {
            Collection<ConfigAttribute> attrs = s.getAllConfigAttributes();
            if (attrs != null) {
                set.addAll(attrs);
            }
        }
        return set;
    }

    public List<MethodSecurityMetadataSource> getMethodSecurityMetadataSources() {
        return methodSecurityMetadataSources;
    }

    // ~ Inner Classes
    // ==================================================================================================

    private static class DefaultCacheKey {
        private final Method method;
        private final Class<?> targetClass;

        DefaultCacheKey(Method method, Class<?> targetClass) {
            this.method = method;
            this.targetClass = targetClass;
        }

        @Override
        public boolean equals(Object other) {
            DefaultCacheKey otherKey = (DefaultCacheKey) other;
            return (this.method.equals(otherKey.method) && ObjectUtils.nullSafeEquals(this.targetClass, otherKey.targetClass));
        }

        @Override
        public int hashCode() {
            return this.method.hashCode() * 21
                    + (this.targetClass != null ? this.targetClass.hashCode() : 0);
        }

        @Override
        public String toString() {
            return "CacheKey[" + (targetClass == null ? "-" : targetClass.getName()) + "; " + method + "]";
        }
    }
}
