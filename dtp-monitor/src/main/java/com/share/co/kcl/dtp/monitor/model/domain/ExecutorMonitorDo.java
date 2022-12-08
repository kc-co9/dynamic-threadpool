package com.share.co.kcl.dtp.monitor.model.domain;

import com.alibaba.fastjson2.JSON;
import com.share.co.kcl.dtp.common.exception.BusinessException;
import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.common.model.dto.ExecutorConfigReportDto;
import com.share.co.kcl.dtp.common.model.bo.ExecutorStatisticsBo;
import com.share.co.kcl.dtp.common.model.dto.ExecutorStatisticsReportDto;
import com.share.co.kcl.dtp.common.utils.FunctionUtils;
import com.share.co.kcl.dtp.monitor.factory.SpringDomainFactory;
import com.share.co.kcl.dtp.monitor.model.Domain;
import com.share.co.kcl.dtp.monitor.model.po.entity.DtpExecutorStatisticsHistory;
import com.share.co.kcl.dtp.monitor.service.DtpExecutorStatisticsHistoryService;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings({"UnnecessaryLocalVariable", "SameParameterValue", "unused"})
public class ExecutorMonitorDo extends Domain {

    private static final ThreadPoolExecutor CLEAR_MONITOR_THREAD_POOL =
            new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadPoolExecutor.DiscardPolicy());

    private static final String SERVER_EXECUTOR_CONFIG_SETTING = "server_executor_config_setting:%s:%s";
    private static final String SERVER_EXECUTOR_CONFIG_MONITOR = "server_executor_config_monitor:%s:%s";
    private static final String SERVER_EXECUTOR_STATISTICS_MONITOR = "server_executor_statistics_monitor:%s:%s";

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
     * the redis key which is used to store exeuctor config setting
     */
    private final String settingExecutorConfigRedis;

    /**
     * the redis key which is used to store executor config info.
     */
    private final String monitorExecutorConfigRedis;

    /**
     * the redis key which is used to store executor statistics.
     */
    private final String monitorExecutorStatisticsRedis;

    /**
     * the executor monitor state
     * <p>
     * The monitorState provides the main lifecycle control, taking on values:
     * <p>
     * INIT:    the executor monitor created by invoker is setting params, and it not accepts reported data.
     * RUNNING: the executor monitor can accept reported data.
     * CLOSE:   the executor monitor can't accept reported data, because of closing by invoker.
     */
    private final AtomicInteger monitorState = new AtomicInteger(INIT);

    @Getter
    private final Long serverId;
    @Getter
    private final String serverIp;

    public ExecutorMonitorDo(Long serverId, String serverIp, ApplicationContext applicationContext) {
        super(applicationContext);
        this.serverId = serverId;
        this.serverIp = serverIp;
        this.serverMonitor = this.getSpringDomainFactory().newServerMonitor(serverId);
        this.settingExecutorConfigRedis = String.format(SERVER_EXECUTOR_CONFIG_SETTING, this.serverId, this.serverIp);
        this.monitorExecutorConfigRedis = String.format(SERVER_EXECUTOR_CONFIG_MONITOR, this.serverId, this.serverIp);
        this.monitorExecutorStatisticsRedis = String.format(SERVER_EXECUTOR_STATISTICS_MONITOR, this.serverId, this.serverIp);
    }

    /**
     * start the executor monitor
     */
    public boolean start() {
        if (!this.serverMonitor.isRunning()) {
            return false;
        }
        this.monitorState.set(RUNNING);
        return true;
    }

    /**
     * save reported data sent by client
     */
    public boolean reportConfig(List<ExecutorConfigReportDto> reportExecutorList) {
        if (RUNNING != this.monitorState.get()) {
            throw new BusinessException("the executor is not running");
        }

        Map<String, ExecutorConfigBo> executorConfigMonitorMap = this.computeExecutorConfigMonitor(reportExecutorList);
        Map<String, ExecutorConfigBo> executorConfigSettingMap = this.computeExecutorConfigSetting(reportExecutorList);

        Boolean success1 = this.saveExecutorHash(this.monitorExecutorConfigRedis, executorConfigMonitorMap);
        Boolean success2 = this.keepExecutorHash(this.monitorExecutorConfigRedis, 10, TimeUnit.MINUTES);

        Boolean success3 = this.saveExecutorHash(this.settingExecutorConfigRedis, executorConfigSettingMap);
        Boolean success4 = this.keepExecutorHash(this.settingExecutorConfigRedis, 10, TimeUnit.MINUTES);

        CLEAR_MONITOR_THREAD_POOL.execute(() -> {
            this.clearExecutorConfigMonitor(reportExecutorList);
            this.clearExecutorConfigSetting(reportExecutorList);
        });

        return Boolean.TRUE.equals(success1)
                && Boolean.TRUE.equals(success2)
                && Boolean.TRUE.equals(success3)
                && Boolean.TRUE.equals(success4);
    }

    public boolean reportStatistics(List<ExecutorStatisticsReportDto> reportExecutorList) {
        if (RUNNING != this.monitorState.get()) {
            throw new BusinessException("the executor is not running");
        }

        Map<String, ExecutorStatisticsBo> executorStatisticsMap = this.computeExecutorStatistics(reportExecutorList);
        Boolean success = this.saveExecutorHashWithTimeout(this.monitorExecutorStatisticsRedis, executorStatisticsMap, 1, TimeUnit.HOURS);

        List<DtpExecutorStatisticsHistory> dtpExecutorStatisticsHistories = FunctionUtils.mappingList(new ArrayList<>(executorStatisticsMap.values()), executorStatisticsBo -> {
            DtpExecutorStatisticsHistory dtpExecutorStatisticsHistory = new DtpExecutorStatisticsHistory();
            dtpExecutorStatisticsHistory.setServerId(serverId);
            dtpExecutorStatisticsHistory.setServerIp(serverIp);
            dtpExecutorStatisticsHistory.setServerName("");
            dtpExecutorStatisticsHistory.setExecutorId(executorStatisticsBo.getExecutorId());
            dtpExecutorStatisticsHistory.setExecutorName(executorStatisticsBo.getExecutorName());
            dtpExecutorStatisticsHistory.setExecutorQueueClass(executorStatisticsBo.getQueueClass());
            dtpExecutorStatisticsHistory.setExecutorQueueNodeCount(executorStatisticsBo.getQueueNodeCount());
            dtpExecutorStatisticsHistory.setExecutorQueueRemainCapacity(executorStatisticsBo.getQueueRemainingCapacity());
            dtpExecutorStatisticsHistory.setExecutorPoolSize(executorStatisticsBo.getPoolSize());
            dtpExecutorStatisticsHistory.setExecutorLargestPoolSize(executorStatisticsBo.getLargestPoolSize());
            dtpExecutorStatisticsHistory.setExecutorActiveCount(executorStatisticsBo.getActiveCount());
            dtpExecutorStatisticsHistory.setExecutorTaskCount(executorStatisticsBo.getTaskCount());
            dtpExecutorStatisticsHistory.setExecutorCompletedTaskCount(executorStatisticsBo.getCompletedTaskCount());
            return dtpExecutorStatisticsHistory;
        });
        this.getDtpExecutorStatisticsHistoryService().saveBatchIgnoreEmpty(dtpExecutorStatisticsHistories);

        return Boolean.TRUE.equals(success);
    }

    /**
     * configure the executor setting
     */
    public boolean configure(String executorId, ExecutorConfigBo executorConfig) {
        if (RUNNING != this.monitorState.get()) {
            throw new BusinessException("the executor is not running");
        }

        ExecutorConfigBo executorConfigMonitor = this.getExecutorConfigMonitor(executorId);
        if (Objects.isNull(executorConfigMonitor)) {
            throw new BusinessException("the executor is not running");
        }

        ExecutorConfigBo executorConfigSetting = this.getExecutorConfigSetting(executorId);
        if (Objects.isNull(executorConfigSetting)) {
            throw new BusinessException("the executor is not running");
        }

        return this.saveExecutorHash(this.settingExecutorConfigRedis, executorId, executorConfig, 10, TimeUnit.MINUTES);
    }

    /**
     * lookup the executor config
     */
    public ExecutorConfigBo lookupConfigSetting(String executorId) {
        if (RUNNING != this.monitorState.get())
            throw new BusinessException("the executor is not running");
        return this.getExecutorConfigSetting(executorId);
    }

    /**
     * lookup the executor config
     */
    public List<ExecutorConfigBo> lookupConfigSetting() {
        if (RUNNING != this.monitorState.get())
            throw new BusinessException("the executor is not running");
        return this.getExecutorConfigSetting();
    }

    /**
     * lookup the executor config
     */
    public ExecutorConfigBo lookupConfigMonitor(String executorId) {
        if (RUNNING != this.monitorState.get())
            throw new BusinessException("the executor is not running");
        return this.getExecutorConfigMonitor(executorId);
    }

    /**
     * lookup the executor config
     */
    public List<ExecutorConfigBo> lookupConfigMonitor() {
        if (RUNNING != this.monitorState.get())
            throw new BusinessException("the executor is not running");
        return this.getExecutorConfigMonitor();
    }

    /**
     * lookup the executor statistics
     */
    public ExecutorStatisticsBo lookupStatistics(String executorId) {
        if (RUNNING != this.monitorState.get())
            throw new BusinessException("the executor is not running");
        return this.getExecutorStatistics(executorId);
    }

    /**
     * check the executor if running
     */
    public boolean isRunning() {
        if (RUNNING != this.monitorState.get()) {
            return false;
        }
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        Boolean isRunning1 = redisTemplate.hasKey(this.monitorExecutorConfigRedis);
        Boolean isRunning2 = redisTemplate.hasKey(this.settingExecutorConfigRedis);
        return Boolean.TRUE.equals(isRunning1)
                && Boolean.TRUE.equals(isRunning2);
    }

    /**
     * close the executor monitor
     */
    public boolean close() {
        if (CLOSE <= this.monitorState.get())
            throw new BusinessException("the executor has been closed");
        if (this.clear()) {
            this.monitorState.set(CLOSE);
            return true;
        }
        return false;
    }

    private boolean saveExecutorHash(String key, String executorId, Object config, long time, TimeUnit timeUnit) {
        if (Objects.isNull(config)) {
            return true;
        }
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        redisTemplate.opsForHash().put(key, executorId, JSON.toJSONString(config));
        Boolean success = redisTemplate.expire(key, time, timeUnit);
        return Boolean.TRUE.equals(success);
    }

    private boolean saveExecutorHashWithTimeout(String key, Map<String, ?> executorConfigMap, long time, TimeUnit timeUnit) {
        boolean success1 = saveExecutorHash(key, executorConfigMap);
        boolean success2 = keepExecutorHash(key, time, timeUnit);
        return Boolean.TRUE.equals(success1) && Boolean.TRUE.equals(success2);
    }

    private boolean saveExecutorHash(String key, Map<String, ?> executorConfigMap) {
        if (MapUtils.isEmpty(executorConfigMap)) {
            return true;
        }
        Map<String, String> value = executorConfigMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, o -> JSON.toJSONString(o.getValue())));
        try {
            RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
            redisTemplate.opsForHash().putAll(key, value);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean keepExecutorHash(String key, long time, TimeUnit timeUnit) {
        Boolean success = this.getRedisTemplate().expire(key, time, timeUnit);
        return Boolean.TRUE.equals(success);
    }

    private void clearExecutorConfigSetting(List<ExecutorConfigReportDto> reportExecutorList) {
        if (CollectionUtils.isEmpty(reportExecutorList)) {
            return;
        }
        Set<String> reportExecutorIds = reportExecutorList.stream().map(ExecutorConfigReportDto::getExecutorId).collect(Collectors.toSet());
        List<ExecutorConfigBo> executorConfigBoList = getExecutorConfigSetting();
        Object[] removeExecutorIds = executorConfigBoList.stream()
                .map(ExecutorConfigBo::getExecutorId)
                .filter(executorId -> !reportExecutorIds.contains(executorId))
                .toArray();
        if (ArrayUtils.isNotEmpty(removeExecutorIds)) {
            this.getRedisTemplate().opsForHash().delete(this.settingExecutorConfigRedis, removeExecutorIds);
        }
    }

    private void clearExecutorConfigMonitor(List<ExecutorConfigReportDto> reportExecutorList) {
        if (CollectionUtils.isEmpty(reportExecutorList)) {
            return;
        }
        Set<String> reportExecutorIds = reportExecutorList.stream().map(ExecutorConfigReportDto::getExecutorId).collect(Collectors.toSet());
        List<ExecutorConfigBo> executorConfigBoList = getExecutorConfigMonitor();
        Object[] removeExecutorIds = executorConfigBoList.stream()
                .map(ExecutorConfigBo::getExecutorId)
                .filter(executorId -> !reportExecutorIds.contains(executorId))
                .toArray();
        if (ArrayUtils.isNotEmpty(removeExecutorIds)) {
            this.getRedisTemplate().opsForHash().delete(this.monitorExecutorConfigRedis, removeExecutorIds);
        }
    }

    private Map<String, ExecutorConfigBo> computeExecutorConfigMonitor(List<ExecutorConfigReportDto> reportExecutorList) {
        return FunctionUtils.mappingMap(reportExecutorList, ExecutorConfigReportDto::getExecutorId, ExecutorConfigReportDto::getExecutorConfig);
    }

    private Map<String, ExecutorConfigBo> computeExecutorConfigSetting(List<ExecutorConfigReportDto> reportExecutorList) {
        List<ExecutorConfigBo> reportConfigList = FunctionUtils.mappingList(reportExecutorList, ExecutorConfigReportDto::getExecutorConfig);
        return reportConfigList.stream()
                .filter(o -> Objects.isNull(this.getExecutorConfigSetting(o.getExecutorId())))
                .map(report -> {
                    ExecutorConfigBo config = new ExecutorConfigBo();
                    config.setExecutorId(report.getExecutorId());
                    config.setExecutorName(report.getExecutorName());
                    config.setCorePoolSize(report.getCorePoolSize());
                    config.setMaximumPoolSize(report.getMaximumPoolSize());
                    config.setKeepAliveTime(report.getKeepAliveTime());
                    config.setRejectedStrategy(report.getRejectedStrategy());
                    return config;
                })
                .collect(Collectors.toMap(ExecutorConfigBo::getExecutorId, Function.identity()));
    }

    private Map<String, ExecutorStatisticsBo> computeExecutorStatistics(List<ExecutorStatisticsReportDto> reportExecutorList) {
        @SuppressWarnings("UnnecessaryLocalVariable")
        Map<String, ExecutorStatisticsBo> executorStatisticsMap = FunctionUtils.mappingMap(reportExecutorList, ExecutorStatisticsReportDto::getExecutorId, ExecutorStatisticsReportDto::getExecutorStatistics);
        return executorStatisticsMap;
    }

    private List<ExecutorConfigBo> getExecutorConfigSetting() {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        Map<Object, Object> redisConfigInfoJson = redisTemplate.opsForHash().entries(this.settingExecutorConfigRedis);
        return redisConfigInfoJson.values()
                .stream()
                .map(String::valueOf)
                .map(json -> JSON.parseObject(json, ExecutorConfigBo.class))
                .collect(Collectors.toList());
    }

    private ExecutorConfigBo getExecutorConfigSetting(String executorId) {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        String redisConfigInfoJson = (String) redisTemplate.opsForHash().get(this.settingExecutorConfigRedis, executorId);
        return JSON.parseObject(redisConfigInfoJson, ExecutorConfigBo.class);
    }

    private List<ExecutorConfigBo> getExecutorConfigMonitor() {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        Map<Object, Object> redisConfigInfoJson = redisTemplate.opsForHash().entries(this.monitorExecutorConfigRedis);
        return redisConfigInfoJson.values()
                .stream()
                .map(String::valueOf)
                .map(json -> JSON.parseObject(json, ExecutorConfigBo.class))
                .collect(Collectors.toList());
    }

    private ExecutorConfigBo getExecutorConfigMonitor(String executorId) {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        String redisConfigInfoJson = (String) redisTemplate.opsForHash().get(this.monitorExecutorConfigRedis, executorId);
        return JSON.parseObject(redisConfigInfoJson, ExecutorConfigBo.class);
    }

    private ExecutorStatisticsBo getExecutorStatistics(String executorId) {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        String redisConfigInfoJson = (String) redisTemplate.opsForHash().get(this.monitorExecutorStatisticsRedis, executorId);
        return JSON.parseObject(redisConfigInfoJson, ExecutorStatisticsBo.class);
    }

    private List<ExecutorStatisticsBo> getExecutorStatistics() {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        Map<Object, Object> redisConfigInfoJson = redisTemplate.opsForHash().entries(this.monitorExecutorStatisticsRedis);
        return redisConfigInfoJson.values()
                .stream()
                .map(String::valueOf)
                .map(json -> JSON.parseObject(json, ExecutorStatisticsBo.class))
                .collect(Collectors.toList());
    }

    /**
     * clear the previous status
     */
    private boolean clear() {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        Boolean success1 = redisTemplate.delete(this.settingExecutorConfigRedis);
        Boolean success2 = redisTemplate.delete(this.monitorExecutorConfigRedis);
        Boolean success3 = redisTemplate.delete(this.monitorExecutorStatisticsRedis);
        return Boolean.TRUE.equals(success1)
                && Boolean.TRUE.equals(success2)
                && Boolean.TRUE.equals(success3);
    }

    @SuppressWarnings("unchecked")
    private RedisTemplate<String, String> getRedisTemplate() {
        return (RedisTemplate<String, String>) this.applicationContext.getBean("dtpStringRedisTemplate");
    }

    private DtpExecutorStatisticsHistoryService getDtpExecutorStatisticsHistoryService() {
        return this.applicationContext.getBean(DtpExecutorStatisticsHistoryService.class);
    }

    private SpringDomainFactory getSpringDomainFactory() {
        return this.applicationContext.getBean(SpringDomainFactory.class);
    }

}
