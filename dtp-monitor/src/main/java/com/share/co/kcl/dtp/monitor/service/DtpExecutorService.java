package com.share.co.kcl.dtp.monitor.service;

import com.share.co.kcl.dtp.common.enums.SyncStatus;
import com.share.co.kcl.dtp.common.exception.BusinessException;
import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.common.model.bo.ExecutorStatisticsBo;
import com.share.co.kcl.dtp.common.model.dto.ExecutorConfigReportDto;
import com.share.co.kcl.dtp.common.model.dto.ExecutorStatisticsReportDto;
import com.share.co.kcl.dtp.monitor.processor.annotation.Lock;
import com.share.co.kcl.dtp.monitor.factory.SpringDomainFactory;
import com.share.co.kcl.dtp.monitor.model.domain.ExecutorMonitorDo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DtpExecutorService {

    private final SpringDomainFactory springDomainFactory;

    @Lock(key = "#serverId + ':' + #serverIp", timeout = 3L, waittime = 1L)
    public void reportExecutorConfig(Long serverId, String serverIp, List<ExecutorConfigReportDto> executorList) {
        ExecutorMonitorDo executorMonitorDo = springDomainFactory.newExecutorMonitor(serverId, serverIp);

        boolean isRunning = executorMonitorDo.isRunning() || executorMonitorDo.start();
        if (!isRunning) {
            throw new BusinessException("report error, executor is down");
        }

        boolean isReport = executorMonitorDo.reportConfig(executorList);
        if (!isReport) {
            throw new BusinessException("executor report failure");
        }
    }

    @Lock(key = "#serverId + ':' + #serverIp", timeout = 3L, waittime = 1L)
    public void reportExecutorStatistics(Long serverId, String serverIp, List<ExecutorStatisticsReportDto> executorStatisticsList) {
        ExecutorMonitorDo executorMonitorDo = springDomainFactory.newExecutorMonitor(serverId, serverIp);

        boolean isRunning = executorMonitorDo.isRunning() || executorMonitorDo.start();
        if (!isRunning) {
            throw new BusinessException("report error, executor is down");
        }

        boolean isReport = executorMonitorDo.reportStatistics(executorStatisticsList);
        if (!isReport) {
            throw new BusinessException("executor report failure");
        }
    }

    public boolean checkExecutorUpdate(Long serverId, String serverIp) {
        ExecutorMonitorDo executorMonitorDo = springDomainFactory.newExecutorMonitor(serverId, serverIp);

        boolean isRunning = executorMonitorDo.isRunning();
        if (!isRunning) {
            throw new BusinessException("report error, executor is down");
        }

        Map<String, String> executorSyncStatusMap = executorMonitorDo.lookupSyncStatus();
        return executorSyncStatusMap.entrySet().stream().anyMatch(entry -> SyncStatus.WAITING.name().equals(entry.getValue()));
    }

    public List<ExecutorConfigBo> lookupExecutorInfo(Long serverId, String serverIp) {
        ExecutorMonitorDo executorMonitorDo = springDomainFactory.newExecutorMonitor(serverId, serverIp);

        boolean isRunning = executorMonitorDo.isRunning();
        if (!isRunning) {
            throw new BusinessException("report error, executor is down");
        }

        return executorMonitorDo.lookupConfig();
    }

    public List<ExecutorStatisticsBo> lookupExecutorStatistics(Long serverId, String serverIp) {
        ExecutorMonitorDo executorMonitorDo = springDomainFactory.newExecutorMonitor(serverId, serverIp);

        boolean isRunning = executorMonitorDo.isRunning();
        if (!isRunning) {
            throw new BusinessException("report error, executor is down");
        }

        return executorMonitorDo.lookupStatistics();
    }

    @Lock(key = "#serverId + ':' + #serverIp", timeout = 3L)
    public boolean configureExecutor(Long serverId, String serverIp, String executorId, ExecutorConfigBo executorConfig) {
        ExecutorMonitorDo executorMonitorDo = springDomainFactory.newExecutorMonitor(serverId, serverIp);

        boolean isRunning = executorMonitorDo.isRunning();
        if (!isRunning) {
            throw new BusinessException("report error, executor is down");
        }

        return executorMonitorDo.configure(executorId, executorConfig);
    }


}
