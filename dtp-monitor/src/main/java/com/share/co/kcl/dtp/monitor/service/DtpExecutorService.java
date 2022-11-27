package com.share.co.kcl.dtp.monitor.service;

import com.share.co.kcl.dtp.common.exception.BusinessException;
import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.common.model.bo.ExecutorStatisticsBo;
import com.share.co.kcl.dtp.common.model.dto.ExecutorReportDto;
import com.share.co.kcl.dtp.monitor.processor.annotation.Lock;
import com.share.co.kcl.dtp.monitor.factory.SpringDomainFactory;
import com.share.co.kcl.dtp.monitor.model.domain.ExecutorMonitorDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DtpExecutorService {

    @Autowired
    private SpringDomainFactory springDomainFactory;

    @Lock(key = "#serverId + ':' + #serverIp", timeout = 3L, waittime = 1L)
    public void reportExecutorInfo(Long serverId, String serverIp, List<ExecutorReportDto> executorList) {
        ExecutorMonitorDo executorMonitorDo = springDomainFactory.newExecutorMonitor(serverId, serverIp);

        boolean isRunning = executorMonitorDo.isRunning() || executorMonitorDo.start();
        if (!isRunning) {
            throw new BusinessException("report error, executor is down");
        }

        boolean isReport = executorMonitorDo.report(executorList);
        if (!isReport) {
            throw new BusinessException("executor report failure");
        }
    }

    public boolean checkExecutorSync(Long serverId, String serverIp) {
        ExecutorMonitorDo executorMonitorDo = springDomainFactory.newExecutorMonitor(serverId, serverIp);

        boolean isRunning = executorMonitorDo.isRunning();
        if (!isRunning) {
            throw new BusinessException("report error, executor is down");
        }

        Map<String, Boolean> executorSyncMap = executorMonitorDo.lookupSync();
        return executorSyncMap.entrySet().stream().anyMatch(entry -> Boolean.FALSE.equals(entry.getValue()));
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
    public boolean configureExecutor(Long serverId, String serverIp, String executorId, ExecutorConfigBo configBody) {
        ExecutorMonitorDo executorMonitorDo = springDomainFactory.newExecutorMonitor(serverId, serverIp);

        boolean isRunning = executorMonitorDo.isRunning();
        if (!isRunning) {
            throw new BusinessException("report error, executor is down");
        }

        return executorMonitorDo.configure(executorId, configBody);
    }


}
