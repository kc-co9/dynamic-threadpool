package com.share.co.kcl.dtp.monitor.model.domain;

import com.share.co.kcl.dtp.common.exception.BusinessException;
import com.share.co.kcl.dtp.common.utils.DateUtils;
import com.share.co.kcl.dtp.monitor.model.Domain;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ServerMonitorDo extends Domain {

    private static final String SERVER_HEALTH_MONITOR = "server_health_monitor:%s";

    // the server monitor created by invoker is setting params, and it not accepts reported data.
    private static final int INIT = 0;
    // the server monitor can accept reported data.
    private static final int RUNNING = 1;
    // the server monitor can't accept reported data, because of closing by invoker.
    private static final int CLOSE = 2;

    /**
     * the redis key which is used to store value.
     */
    private final String monitorRedis;

    /**
     * the server monitor state
     * <p>
     * The monitorState provides the main lifecycle control, taking on values:
     * <p>
     * INIT:    the server monitor created by invoker is setting params, and it not accepts reported data.
     * RUNNING: the server monitor can accept reported data.
     * CLOSE:   the server monitor can't accept reported data, because of closing by invoker.
     */
    private final AtomicInteger monitorState = new AtomicInteger(INIT);

    /**
     * unique server id
     */
    @Getter
    private final Long serverId;

    public ServerMonitorDo(Long serverId, ApplicationContext applicationContext) {
        super(applicationContext);
        this.serverId = serverId;
        this.monitorRedis = String.format(SERVER_HEALTH_MONITOR, this.serverId);
    }

    /**
     * start the server monitor
     */
    public boolean start() {
        this.monitorState.set(RUNNING);
        return true;
    }

    /**
     * save reported data sent by client
     */
    public boolean report(String serverIp) {
        if (RUNNING != this.monitorState.get()) {
            throw new BusinessException("the server is not running");
        }
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        redisTemplate.opsForHash().put(this.monitorRedis, serverIp, String.valueOf(DateUtils.valueOfSecond(DateUtils.after(60, ChronoUnit.SECONDS))));
        Boolean success = redisTemplate.expire(this.monitorRedis, 60, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    /**
     * lookup the reported data sent by client
     *
     * @return the list about server ip
     */
    public List<String> lookup() {
        if (RUNNING != this.monitorState.get()) {
            return Collections.emptyList();
        }
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        Set<Map.Entry<String, Long>> reportList = redisTemplate.opsForHash().entries(this.monitorRedis)
                .entrySet()
                .stream()
                .map(o -> new AbstractMap.SimpleEntry<>(String.valueOf(o.getKey()), Long.parseLong((String) o.getValue())))
                .collect(Collectors.toSet());

        Object[] removeList = reportList.stream()
                .filter(o -> DateUtils.valueOfSecond(DateUtils.now()) > o.getValue())
                .map(Map.Entry::getKey)
                .toArray();
        if (ArrayUtils.isNotEmpty(removeList)) {
            redisTemplate.opsForHash().delete(this.monitorRedis, removeList);
        }

        return reportList.stream()
                .filter(o -> DateUtils.valueOfSecond(DateUtils.now()) <= o.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * check the server if running
     */
    public boolean isRunning() {
        if (RUNNING != this.monitorState.get()) {
            return false;
        }
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        Boolean isRunning = redisTemplate.hasKey(this.monitorRedis);
        return Boolean.TRUE.equals(isRunning);
    }

    /**
     * close the server monitor
     */
    public boolean close() {
        if (CLOSE <= this.monitorState.get()) {
            throw new BusinessException("the server has been closed");
        }
        if (this.clear()) {
            this.monitorState.set(CLOSE);
            return true;
        }
        return false;
    }

    /**
     * clear the previous status
     */
    private boolean clear() {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        Boolean success = redisTemplate.delete(this.monitorRedis);
        return Boolean.TRUE.equals(success);
    }

    @SuppressWarnings("unchecked")
    private RedisTemplate<String, String> getRedisTemplate() {
        return (RedisTemplate<String, String>) this.applicationContext.getBean("dtpStringRedisTemplate");
    }

}
