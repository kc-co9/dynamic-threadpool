package com.share.co.kcl.dtp.monitor.model.domain;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.share.co.kcl.dtp.common.enums.SyncStatus;
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
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ExecutorMonitorDo extends Domain {

    private static final String SERVER_EXECUTOR_CONFIG_MONITOR = "server_executor_config_monitor:%s";
    private static final String SERVER_EXECUTOR_SYNC_STATUS_MONITOR = "server_executor_sync_status_monitor:%s";
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
    private final String monitorExecutorConfigRedis;

    /**
     * the redis key which is used to store executor config sync.
     */
    private final String monitorExecutorSyncStatusRedis;

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
        this.monitorExecutorConfigRedis = String.format(SERVER_EXECUTOR_CONFIG_MONITOR, this.serverId);
        this.monitorExecutorSyncStatusRedis = String.format(SERVER_EXECUTOR_SYNC_STATUS_MONITOR, this.serverId);
        this.monitorExecutorStatisticsRedis = String.format(SERVER_EXECUTOR_STATISTICS_MONITOR, this.serverId);
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

        List<ExecutorConfigBo> executorConfigList = this.computeExecutorConfig(reportExecutorList);
        Map<String, String> executorSyncStatusMap = this.computeExecutorSyncStatus(reportExecutorList);

        Boolean success1 = this.saveExecutorHash(this.monitorExecutorConfigRedis, executorConfigList, 1, TimeUnit.HOURS);
        Boolean success2 = this.saveExecutorHash(this.monitorExecutorSyncStatusRedis, executorSyncStatusMap, 1, TimeUnit.HOURS);

        return Boolean.TRUE.equals(success1) && Boolean.TRUE.equals(success2);
    }

    public boolean reportStatistics(List<ExecutorStatisticsReportDto> reportExecutorList) {
        if (RUNNING != this.monitorState.get()) {
            throw new BusinessException("the executor is not running");
        }

        List<ExecutorStatisticsBo> executorStatisticsList = this.computeExecutorStatistics(reportExecutorList);
        Boolean success = this.saveExecutorHash(this.monitorExecutorStatisticsRedis, executorStatisticsList, 1, TimeUnit.HOURS);

        List<DtpExecutorStatisticsHistory> dtpExecutorStatisticsHistories = FunctionUtils.mappingList(executorStatisticsList, executorStatisticsBo -> {
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

        List<ExecutorConfigBo> executorConfigList = this.getExecutorConfig();
        Map<String, String> executorSyncStatusMap = this.getExecutorSyncStatus();

        // replace
        for (int i = 0; i < executorConfigList.size(); i++) {
            ExecutorConfigBo redisConfigInfo = executorConfigList.get(i);
            if (Objects.equals(executorId, redisConfigInfo.getExecutorId())) {
                executorConfigList.set(i, executorConfig);
                executorSyncStatusMap.put(executorId, String.valueOf(SyncStatus.WAITING));
                break;
            }
        }

        Boolean success1 = this.saveExecutorHash(this.monitorExecutorConfigRedis, executorConfigList, 1, TimeUnit.HOURS);
        Boolean success2 = this.saveExecutorHash(this.monitorExecutorSyncStatusRedis, executorSyncStatusMap, 1, TimeUnit.HOURS);

        return Boolean.TRUE.equals(success1) && Boolean.TRUE.equals(success2);
    }

    /**
     * lookup the executor config
     */
    public List<ExecutorConfigBo> lookupConfig() {
        if (RUNNING != this.monitorState.get())
            throw new BusinessException("the executor is not running");
        return this.getExecutorConfig();
    }

    /**
     * lookup the executor statistics
     */
    public List<ExecutorStatisticsBo> lookupStatistics() {
        if (RUNNING != this.monitorState.get())
            throw new BusinessException("the executor is not running");
        return this.getExecutorStatistics();
    }

    /**
     * lookup the executor sync
     */
    public Map<String, String> lookupSyncStatus() {
        if (RUNNING != this.monitorState.get())
            throw new BusinessException("the executor is not running");
        return this.getExecutorSyncStatus();
    }

    /**
     * check the executor if sync
     */
    public boolean isChange(String executorId) {
        if (RUNNING != this.monitorState.get()) {
            throw new BusinessException("the executor is not running");
        }
        Map<String, String> redisConfigSyncMap = this.getExecutorSyncStatus();
        return SyncStatus.WAITING.name().equals(redisConfigSyncMap.get(executorId));
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
        Boolean isRunning2 = redisTemplate.hasKey(this.monitorExecutorSyncStatusRedis);
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

    @SuppressWarnings("SameParameterValue")
    private boolean saveExecutorHash(String key, Object value, long time, TimeUnit timeUnit) {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        redisTemplate.opsForHash().put(key, this.serverIp, JSON.toJSONString(value));
        Boolean success = redisTemplate.expire(key, time, timeUnit);
        return Boolean.TRUE.equals(success);
    }

    private List<ExecutorConfigBo> computeExecutorConfig(List<ExecutorConfigReportDto> reportExecutorList) {
        List<ExecutorConfigBo> redisConfigInfoList = this.getExecutorConfig();
        List<ExecutorConfigBo> reportConfigInfoList = FunctionUtils.mappingList(reportExecutorList, ExecutorConfigReportDto::getExecutorConfig);

        Map<String, ExecutorConfigBo> redisConfigInfoMap = FunctionUtils.mappingMap(redisConfigInfoList, ExecutorConfigBo::getExecutorId, Function.identity());
        for (ExecutorConfigBo executorConfigBo : reportConfigInfoList) {
            ExecutorConfigBo redisConfigBo = redisConfigInfoMap.get(executorConfigBo.getExecutorId());
            if (Objects.nonNull(redisConfigBo)) {
                executorConfigBo.setExecutorId(redisConfigBo.getExecutorId());
                executorConfigBo.setExecutorName(redisConfigBo.getExecutorName());
                executorConfigBo.setCorePoolSize(redisConfigBo.getCorePoolSize());
                executorConfigBo.setMaximumPoolSize(redisConfigBo.getMaximumPoolSize());
                executorConfigBo.setKeepAliveTime(redisConfigBo.getKeepAliveTime());
                executorConfigBo.setRejectedStrategy(redisConfigBo.getRejectedStrategy());
            }
        }
        return reportConfigInfoList;
    }

    private Map<String, String> computeExecutorSyncStatus(List<ExecutorConfigReportDto> reportExecutorList) {

        Map<String, String> result = new HashMap<>();

        List<ExecutorConfigBo> redisConfigInfoList = this.getExecutorConfig();
        Map<String, ExecutorConfigBo> redisConfigInfoMap = FunctionUtils.mappingMap(redisConfigInfoList, ExecutorConfigBo::getExecutorId, Function.identity());

        List<ExecutorConfigBo> reportConfigInfoList = FunctionUtils.mappingList(reportExecutorList, ExecutorConfigReportDto::getExecutorConfig);

        for (ExecutorConfigBo reportConfig : reportConfigInfoList) {
            ExecutorConfigBo redisConfig = redisConfigInfoMap.get(reportConfig.getExecutorId());
            if (Objects.isNull(redisConfig)) {
                result.put(reportConfig.getExecutorId(), String.valueOf(SyncStatus.FINISH));
                continue;
            }
            if (Objects.equals(redisConfig.getExecutorName(), reportConfig.getExecutorName())
                    && Objects.equals(redisConfig.getCorePoolSize(), reportConfig.getCorePoolSize())
                    && Objects.equals(redisConfig.getMaximumPoolSize(), reportConfig.getMaximumPoolSize())
                    && Objects.equals(redisConfig.getKeepAliveTime(), reportConfig.getKeepAliveTime())
                    && Objects.equals(redisConfig.getRejectedStrategy(), reportConfig.getRejectedStrategy())) {
                result.put(reportConfig.getExecutorId(), String.valueOf(SyncStatus.FINISH));
                continue;
            }

            result.put(reportConfig.getExecutorId(), String.valueOf(SyncStatus.WAITING));
        }
        return result;
    }

    private List<ExecutorStatisticsBo> computeExecutorStatistics(List<ExecutorStatisticsReportDto> reportExecutorList) {
        @SuppressWarnings("UnnecessaryLocalVariable")
        List<ExecutorStatisticsBo> executorStatisticsList = FunctionUtils.mappingList(reportExecutorList, ExecutorStatisticsReportDto::getExecutorStatistics);
        return executorStatisticsList;
    }

    private List<ExecutorConfigBo> getExecutorConfig() {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        String redisConfigInfoJson = (String) redisTemplate.opsForHash().get(this.monitorExecutorConfigRedis, this.serverIp);
        List<ExecutorConfigBo> redisConfigInfoList = JSON.parseObject(redisConfigInfoJson, new TypeReference<List<ExecutorConfigBo>>() {
        });
        return Optional.ofNullable(redisConfigInfoList).orElse(new ArrayList<>());
    }

    private Map<String, String> getExecutorSyncStatus() {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        String configSyncStatusJson = (String) redisTemplate.opsForHash().get(this.monitorExecutorSyncStatusRedis, this.serverIp);
        Map<String, String> configSyncStatusMap = JSON.parseObject(configSyncStatusJson, new TypeReference<Map<String, String>>() {
        });
        return Optional.ofNullable(configSyncStatusMap).orElse(new HashMap<>());
    }

    private List<ExecutorStatisticsBo> getExecutorStatistics() {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        String redisConfigInfoJson = (String) redisTemplate.opsForHash().get(this.monitorExecutorStatisticsRedis, this.serverIp);
        List<ExecutorStatisticsBo> redisStatisticsBos = JSON.parseObject(redisConfigInfoJson, new TypeReference<List<ExecutorStatisticsBo>>() {
        });
        return Optional.ofNullable(redisStatisticsBos).orElse(new ArrayList<>());
    }

    /**
     * clear the previous status
     */
    private boolean clear() {
        RedisTemplate<String, String> redisTemplate = this.getRedisTemplate();
        Boolean success1 = redisTemplate.delete(this.monitorExecutorConfigRedis);
        Boolean success2 = redisTemplate.delete(this.monitorExecutorSyncStatusRedis);
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
