package com.share.co.kcl.dtp.monitor.sdk;

import com.alibaba.fastjson2.TypeReference;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface CacheClient {

    /**
     * 设置缓存
     *
     * @param key
     *            缓存KEY
     * @param value
     *            缓存VALUE
     */
    void set(String key, Object value);

    /**
     * 设置缓存
     *
     * @param key
     *            缓存KEY
     * @param value
     *            缓存VALUE
     * @param timeout
     *            过期时间
     * @param timeUnit
     *            时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit timeUnit);

    /**
     * 获取缓存
     *
     * @param key
     *            缓存KEY
     * @param clazz
     *            缓存类型
     * @param <T>
     *            泛型-返回值
     * @return 返回对象
     */
    <T> Optional<T> get(String key, Class<T> clazz);

    /**
     * 获取缓存
     *
     * @param key
     *            缓存KEY
     * @param typeReference
     *            缓存类型
     * @param <T>
     *            泛型-返回值
     * @return 返回对象
     */
    <T> Optional<T> get(String key, TypeReference<T> typeReference);

    /**
     * 删除缓存
     *
     * @param key
     *            缓存KEY
     * @return
     */
    Boolean remove(String key);

    /**
     * 批量删除缓存
     *
     * @param keyList
     *            KEY列表
     */
    void remove(List<String> keyList);

    /**
     * 自增,用于数字类型缓存
     *
     * @param key
     *            缓存KEY
     * @return 返回自增后的值
     */
    Long increment(String key);

    /**
     * 自增,用于数字类型缓存
     *
     * @param key
     *            缓存KEY
     * @param step
     *            自增步数
     * @param expireTime
     *            过期时间(单位:秒)
     * @return 返回自增后的值
     */
    Long increment(String key, long step, long expireTime);

    /**
     * 是否存在KEY
     *
     * @param key
     *            缓存KEY
     * @return true/false
     */
    boolean hasKey(String key);

    /**
     * 获取超时时间
     *
     * @param key
     *            缓存KEY
     * @param timeUnit
     *            时间单位
     * @return 返回过期时间
     */
    Long getExpire(String key, TimeUnit timeUnit);

    /**
     * 刷新缓存时间
     *
     * @param key
     *            缓存KEY
     * @param timeout
     *            过期时间
     * @param timeUnit
     *            时间单位
     * @return 刷新是否成功
     */
    Boolean refreshExpire(String key, long timeout, TimeUnit timeUnit);

}
