package com.share.co.kcl.dtp.spring.meta;

import java.util.concurrent.atomic.AtomicReference;

public class DynamicPoolConfigRepository {
    private static final AtomicReference<DynamicPoolConfig> DYNAMIC_POOL_CONFIG = new AtomicReference<>();

    private DynamicPoolConfigRepository() {
    }

    public static void save(DynamicPoolConfig dynamicPoolConfig) {
        DynamicPoolConfigRepository.DYNAMIC_POOL_CONFIG.set(dynamicPoolConfig);
    }

    public static DynamicPoolConfig get() {
        return DYNAMIC_POOL_CONFIG.get();
    }
}
