package com.share.co.kcl.dtp.monitor.sdk.helper;

import com.alibaba.fastjson2.TypeReference;
import com.share.co.kcl.dtp.monitor.sdk.CacheClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class CacheHelper {

    private static CacheClient cacheClient;

    @Autowired
    public CacheHelper(CacheClient cacheClient) {
        CacheHelper.cacheClient = cacheClient;
    }

    public static void set(String key, Object value) {
        cacheClient.set(key, value);
    }

    public static void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        cacheClient.set(key, value, timeout, timeUnit);
    }

    public static <T> Optional<T> get(String key, Class<T> clazz) {
        return cacheClient.get(key, clazz);
    }

    public static <T> Optional<T> get(String key, TypeReference<T> typeReference) {
        return cacheClient.get(key, typeReference);
    }

    public static Boolean remove(String key) {
        return cacheClient.remove(key);
    }

    public static void remove(List<String> keyList) {
        cacheClient.remove(keyList);
    }

    public static Long increment(String key) {
        return cacheClient.increment(key);
    }

    public static Long increment(String key, long step, long expireTime) {
        return cacheClient.increment(key, step, expireTime);
    }

    public static boolean hasKey(String key) {
        return cacheClient.hasKey(key);
    }

    public static Long getExpire(String key, TimeUnit timeUnit) {
        return cacheClient.getExpire(key, timeUnit);
    }

    public static Boolean refreshExpire(String key, long timeout, TimeUnit timeUnit) {
        return cacheClient.refreshExpire(key, timeout, timeUnit);
    }
}
