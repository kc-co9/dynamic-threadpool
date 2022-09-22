package com.share.co.kcl.dtp.monitor.model.domain;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.share.co.kcl.dtp.common.exception.BusinessException;
import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.common.model.dto.ExecutorReportDto;
import com.share.co.kcl.dtp.common.model.bo.ExecutorStatisticsBo;
import com.share.co.kcl.dtp.common.utils.FunctionUtils;
import com.share.co.kcl.dtp.monitor.model.Domain;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ExecutorMonitorDo extends Domain {

    private static final String SERVER_EXECUTOR_CONFIG_INFO_MONITOR = "server_executor_config_info_monitor:%s";
    private static final String SERVER_EXECUTOR_CONFIG_SYNC_MONITOR = "server_executor_config_sync_monitor:%s";
    private static final String SERVER_EXECUTOR_STATISTICS_MONITOR = "server_executor_statistics_monitor:%s";

    // the executor monitor created by invoker is setting params, and it not accepts reported data.
    private static final int INIT = 0;
    // the executor monitor can accept reported data.
    private static final int RUNNING = 1;
    // the executor monitor can't accept reported data, because of closing by invoker.
    private static final int CLOSE = 2;

    /**
     * the server monitor
     */
    private final ServerMonitorDo serverMonitor;

    /**
     * the redis key which is used to store executor config info.
     */
    private final String monitorConfigInfoRedis;

    /**
     * the redis key which is used to store executor config sync.
     */
    private final String monitorConfigSyncRedis;

    /**
     * the redis key which is used to store executor statistics.
     */
    private final String monitorStatisticsRedis;

    /**
     * the executor monitor state
     * <p>
     * The monitorState provides the main lifecycle control, taking on values:
     * <p>
     * INIT:    the executor monitor created by invoker is setting params, and it not accepts reported data.
     * RUNNING: the executor monitor can accept reported data.
     * CLOSE:   the executor monitor can't accept reported data, because of closing by invoker.
     */
    private static final AtomicInteger monitorState = new AtomicInteger(INIT);

    @Getter
    private final Long serverId;
    @Getter
    private final String serverIp;

    public ExecutorMonitorDo(Long serverId, String serverIp, ApplicationContext applicationContext) {
        super(applicationContext);
        this.serverId = serverId;
        this.serverIp = serverIp;
        this.serverMonitor = new ServerMonitorDo(serverId, applicationContext);
        this.monitorConfigInfoRedis = String.format(SERVER_EXECUTOR_CONFIG_INFO_MONITOR, this.serverId);
        this.monitorConfigSyncRedis = String.format(SERVER_EXECUTOR_CONFIG_SYNC_MONITOR, this.serverId);
        this.monitorStatisticsRedis = String.format(SERVER_EXECUTOR_STATISTICS_MONITOR, this.serverId);
    }

    /**
     * start the executor monitor
     */
    public boolean start() {
        if (!this.serverMonitor.isRunning()) {
            return false;
        }
        if (!monitorState.compareAndSet(INIT, RUNNING)) {
            return false;
        }
        return true;
    }

    /**
     * save reported data sent by client
     */
    public boolean report(List<ExecutorReportDto> reportExecutorList) {
        if (RUNNING != monitorState.get()) {
            throw new BusinessException("the executor is not running");
        }

        List<ExecutorConfigBo> redisConfigInfoList = this.computeRedisConfigInfo(reportExecutorList);
        Map<String, Boolean> redisConfigSyncMap = this.computeRedisConfigSync(reportExecutorList, redisConfigInfoList);
        List<ExecutorStatisticsBo> reportStatisticsList = FunctionUtils.mappingList(reportExecutorList, ExecutorReportDto::getStatisticsBody);

        Boolean success1 = this.saveExecutorHash(this.monitorConfigInfoRedis, redisConfigInfoList, 1, TimeUnit.HOURS);
        Boolean success2 = this.saveExecutorHash(this.monitorConfigSyncRedis, redisConfigSyncMap, 1, TimeUnit.HOURS);
        Boolean success3 = this.saveExecutorHash(this.monitorStatisticsRedis, reportStatisticsList, 1, TimeUnit.HOURS);

        return Boolean.TRUE.equals(success1)
                && Boolean.TRUE.equals(success2)
                && Boolean.TRUE.equals(success3);
    }

    /**
     * configure the executor setting
     */
    public boolean configure(String executorId, ExecutorConfigBo configUpdateInfo) {
        if (RUNNING != monitorState.get()) {
            throw new BusinessException("the executor is not running");
        }

        List<ExecutorConfigBo> redisConfigInfoList = this.getRedisConfigInfo();
        Map<String, Boolean> redisConfigSyncMap = this.getRedisConfigSync();

        // replace
        for (int i = 0; i < redisConfigInfoList.size(); i++) {
            ExecutorConfigBo redisConfigInfo = redisConfigInfoList.get(i);
            if (Objects.equals(executorId, redisConfigInfo.getExecutorId())) {
                redisConfigInfoList.set(i, configUpdateInfo);
                redisConfigSyncMap.put(executorId, false);
                break;
            }
        }

        Boolean success1 = this.saveExecutorHash(this.monitorConfigInfoRedis, redisConfigInfoList, 1, TimeUnit.HOURS);
        Boolean success2 = this.saveExecutorHash(this.monitorConfigSyncRedis, redisConfigSyncMap, 1, TimeUnit.HOURS);

        return Boolean.TRUE.equals(success1) && Boolean.TRUE.equals(success2);
    }

    /**
     * lookup the executor config
     */
    public List<ExecutorConfigBo> lookupConfig() {
        if (RUNNING != monitorState.get())
            throw new BusinessException("the executor is not running");
        return this.getRedisConfigInfo();
    }

    /**
     * lookup the executor sync
     */
    public Map<String, Boolean> lookupSync() {
        if (RUNNING != monitorState.get())
            throw new BusinessException("the executor is not running");
        return this.getRedisConfigSync();
    }

    /**
     * lookup the executor statistics
     */
    public List<ExecutorStatisticsBo> lookupStatistics() {
        if (RUNNING != monitorState.get())
            throw new BusinessException("the executor is not running");
        return this.getRedisStatistics();
    }

    /**
     * check the executor if sync
     */
    public boolean isSync(String executorId) {
        if (RUNNING != monitorState.get()) {
            throw new BusinessException("the executor is not running");
        }
        Map<String, Boolean> redisConfigSyncMap = this.getRedisConfigSync();
        return Boolean.TRUE.equals(redisConfigSyncMap.get(executorId));
    }

    /**
     * check the executor if running
     */
    public boolean isRunning() {
        if (RUNNING != monitorState.get()) {
            return false;
        }
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        Boolean isRunning1 = redisTemplate.hasKey(this.monitorConfigInfoRedis);
        Boolean isRunning2 = redisTemplate.hasKey(this.monitorConfigSyncRedis);
        Boolean isRunning3 = redisTemplate.hasKey(this.monitorStatisticsRedis);
        return Boolean.TRUE.equals(isRunning1)
                && Boolean.TRUE.equals(isRunning2)
                && Boolean.TRUE.equals(isRunning3);

    }

    /**
     * close the executor monitor
     */
    public boolean close() {
        if (CLOSE <= monitorState.get())
            throw new BusinessException("the executor has been closed");
        if (this.clear()) {
            monitorState.set(CLOSE);
            return true;
        }
        return false;
    }

    @SuppressWarnings("SameParameterValue")
    private boolean saveExecutorHash(String key, Object value, long time, TimeUnit timeUnit) {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        redisTemplate.opsForHash().put(key, this.serverIp, JSON.toJSONString(value));
        Boolean success = redisTemplate.expire(key, time, timeUnit);
        return Boolean.TRUE.equals(success);
    }

    private List<ExecutorConfigBo> computeRedisConfigInfo(List<ExecutorReportDto> reportExecutorList) {
        List<ExecutorConfigBo> redisConfigInfoList = this.getRedisConfigInfo();
        Map<String, ExecutorConfigBo> redisConfigInfoMap = FunctionUtils.mappingMap(redisConfigInfoList, ExecutorConfigBo::getExecutorId, Function.identity());
        for (ExecutorConfigBo reportConfigBody : FunctionUtils.mappingList(reportExecutorList, ExecutorReportDto::getConfigBody)) {
            ExecutorConfigBo redisConfigBody = redisConfigInfoMap.get(reportConfigBody.getExecutorId());
            if (Objects.isNull(redisConfigBody)) {
                redisConfigInfoList.add(reportConfigBody);
            }
        }
        return redisConfigInfoList;
    }

    private Map<String, Boolean> computeRedisConfigSync(List<ExecutorReportDto> reportExecutorList, List<ExecutorConfigBo> redisExecutorConfigList) {

        Map<String, Boolean> redisConfigSyncMap = this.getRedisConfigSync();

        Map<String, ExecutorConfigBo> redisExecutorConfigMap = FunctionUtils.mappingMap(redisExecutorConfigList, ExecutorConfigBo::getExecutorId, Function.identity());

        List<ExecutorConfigBo> reportExecutorConfigList = FunctionUtils.mappingList(reportExecutorList, ExecutorReportDto::getConfigBody);
        for (ExecutorConfigBo reportConfig : reportExecutorConfigList) {
            ExecutorConfigBo redisConfigBody = redisExecutorConfigMap.get(reportConfig.getExecutorId());
            if (Objects.isNull(redisConfigBody)) {
                continue;
            }

            if (Objects.equals(redisConfigBody.getExecutorName(), reportConfig.getExecutorName())
                    && Objects.equals(redisConfigBody.getCorePoolSize(), reportConfig.getCorePoolSize())
                    && Objects.equals(redisConfigBody.getMaximumPoolSize(), reportConfig.getMaximumPoolSize())
                    && Objects.equals(redisConfigBody.getKeepAliveTime(), reportConfig.getKeepAliveTime())
                    && Objects.equals(redisConfigBody.getRejectedStrategy(), reportConfig.getRejectedStrategy())) {
                redisConfigSyncMap.put(reportConfig.getExecutorId(), true);
                continue;
            }

            redisConfigSyncMap.put(reportConfig.getExecutorId(), false);
        }

        return redisConfigSyncMap;
    }

    private List<ExecutorConfigBo> getRedisConfigInfo() {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        String redisConfigInfoJson = (String) redisTemplate.opsForHash().get(this.monitorConfigInfoRedis, this.serverIp);
        List<ExecutorConfigBo> redisConfigInfoList = JSON.parseObject(redisConfigInfoJson, new TypeReference<List<ExecutorConfigBo>>() {
        });
        return Optional.ofNullable(redisConfigInfoList).orElse(new ArrayList<>());
    }

    private Map<String, Boolean> getRedisConfigSync() {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        String configSyncJson = (String) redisTemplate.opsForHash().get(this.monitorConfigSyncRedis, this.serverIp);
        Map<String, Boolean> configSyncMap = JSON.parseObject(configSyncJson, new TypeReference<Map<String, Boolean>>() {
        });
        return Optional.ofNullable(configSyncMap).orElse(new HashMap<>());
    }

    private List<ExecutorStatisticsBo> getRedisStatistics() {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        String redisConfigInfoJson = (String) redisTemplate.opsForHash().get(this.monitorStatisticsRedis, this.serverIp);
        List<ExecutorStatisticsBo> redisStatisticsBos = JSON.parseObject(redisConfigInfoJson, new TypeReference<List<ExecutorStatisticsBo>>() {
        });
        return Optional.ofNullable(redisStatisticsBos).orElse(new ArrayList<>());
    }

    /**
     * clear the previous status
     */
    private boolean clear() {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        Boolean success1 = redisTemplate.delete(this.monitorConfigInfoRedis);
        Boolean success2 = redisTemplate.delete(this.monitorConfigSyncRedis);
        Boolean success3 = redisTemplate.delete(this.monitorStatisticsRedis);
        return Boolean.TRUE.equals(success1)
                && Boolean.TRUE.equals(success2)
                && Boolean.TRUE.equals(success3);
    }

    @SuppressWarnings("unchecked")
    private RedisTemplate<String, String> getRedisTemplate() {
        return (RedisTemplate<String, String>) this.applicationContext.getBean("dtpStringRedisTemplate");
    }


}
