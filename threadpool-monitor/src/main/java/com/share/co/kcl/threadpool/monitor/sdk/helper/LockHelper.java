package com.share.co.kcl.threadpool.monitor.sdk.helper;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class LockHelper {

    private static final Logger LOG = LoggerFactory.getLogger(LockHelper.class);

    private static RedissonClient redissonClient;

    @Autowired
    public LockHelper(RedissonClient redissonClient) {
        LockHelper.redissonClient = redissonClient;
    }

    public static boolean tryLock(String key, Long liveTime) {

        try {
            return redissonClient.getLock(key).tryLock(0, liveTime, TimeUnit.SECONDS);
        } catch (InterruptedException ignore) {
            LOG.error("LockHelper invoke tryLock method failure, key:{}", key, ignore);
        }
        return false;
    }

    public static boolean tryLock(String key) {
        return tryLock(key, 10L);
    }

    /**
     * 尝试加锁
     *
     * @param key      key
     * @param liveTime 锁的缓存时长
     * @param waitTime 等待锁的时间
     * @return 是否锁定成
     */
    public static boolean tryLock(String key, Long liveTime, Long waitTime) {

        try {
            return redissonClient.getLock(key).tryLock(waitTime, liveTime, TimeUnit.SECONDS);
        } catch (InterruptedException ignore) {
            LOG.error("LockHelper invoke tryLock method failure, key:{}", key, ignore);
        }
        return false;
    }

    public static void releaseLock(String key) {
        try {
            RLock lock = redissonClient.getLock(key);
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            } else {
                LOG.warn("LockHelper invoke tryLock method failure because of not existing or not holding , key:{}", key);
            }
        } catch (Exception ignore) {
            // 解锁失败
            LOG.error("LockHelper invoke releaseLock method failure, key:{}", key, ignore);
        }
    }

}
