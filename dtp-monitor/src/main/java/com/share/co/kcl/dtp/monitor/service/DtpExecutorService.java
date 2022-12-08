package com.share.co.kcl.dtp.monitor.service;

import com.share.co.kcl.dtp.common.exception.BusinessException;
import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.common.model.bo.ExecutorStatisticsBo;
import com.share.co.kcl.dtp.common.model.dto.ExecutorConfigReportDto;
import com.share.co.kcl.dtp.common.model.dto.ExecutorStatisticsReportDto;
import com.share.co.kcl.dtp.common.utils.FunctionUtils;
import com.share.co.kcl.dtp.monitor.processor.annotation.Lock;
import com.share.co.kcl.dtp.monitor.factory.SpringDomainFactory;
import com.share.co.kcl.dtp.monitor.model.domain.ExecutorMonitorDo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

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

        List<ExecutorConfigBo> configMonitorList = executorMonitorDo.lookupConfigMonitor();
        List<ExecutorConfigBo> configSettingList = executorMonitorDo.lookupConfigSetting();

        Map<String, ExecutorConfigBo> configMonitorMap = FunctionUtils.mappingMap(configMonitorList, ExecutorConfigBo::getExecutorId, Function.identity());
        Map<String, ExecutorConfigBo> configSettingMap = FunctionUtils.mappingMap(configSettingList, ExecutorConfigBo::getExecutorId, Function.identity());

        for (Map.Entry<String, ExecutorConfigBo> entry : configMonitorMap.entrySet()) {
            ExecutorConfigBo config = entry.getValue();
            ExecutorConfigBo setting = configSettingMap.get(entry.getKey());
            if (!Objects.equals(config.getCorePoolSize(), setting.getCorePoolSize())
                    || !Objects.equals(config.getMaximumPoolSize(), setting.getMaximumPoolSize())
                    || !Objects.equals(config.getKeepAliveTime(), setting.getKeepAliveTime())
                    || !Objects.equals(config.getRejectedStrategy(), setting.getRejectedStrategy())) {
                return true;
            }
        }
        return false;
    }

    public List<ExecutorConfigBo> lookupExecutorUpdate(Long serverId, String serverIp) {
        ExecutorMonitorDo executorMonitorDo = springDomainFactory.newExecutorMonitor(serverId, serverIp);

        boolean isRunning = executorMonitorDo.isRunning();
        if (!isRunning) {
            throw new BusinessException("report error, executor is down");
        }

        List<ExecutorConfigBo> configMonitorList = executorMonitorDo.lookupConfigMonitor();
        List<ExecutorConfigBo> configSettingList = executorMonitorDo.lookupConfigSetting();

        Map<String, ExecutorConfigBo> configMonitorMap = FunctionUtils.mappingMap(configMonitorList, ExecutorConfigBo::getExecutorId, Function.identity());
        Map<String, ExecutorConfigBo> configSettingMap = FunctionUtils.mappingMap(configSettingList, ExecutorConfigBo::getExecutorId, Function.identity());

        List<ExecutorConfigBo> result = new ArrayList<>();
        for (Map.Entry<String, ExecutorConfigBo> entry : configMonitorMap.entrySet()) {
            ExecutorConfigBo config = entry.getValue();
            ExecutorConfigBo setting = configSettingMap.get(entry.getKey());
            if (!Objects.equals(config.getCorePoolSize(), setting.getCorePoolSize())
                    || !Objects.equals(config.getMaximumPoolSize(), setting.getMaximumPoolSize())
                    || !Objects.equals(config.getKeepAliveTime(), setting.getKeepAliveTime())
                    || !Objects.equals(config.getRejectedStrategy(), setting.getRejectedStrategy())) {
                result.add(setting);
            }
        }
        return result;
    }

    public ExecutorConfigBo lookupExecutorConfigSetting(Long serverId, String serverIp, String executorId) {
        ExecutorMonitorDo executorMonitorDo = springDomainFactory.newExecutorMonitor(serverId, serverIp);

        boolean isRunning = executorMonitorDo.isRunning();
        if (!isRunning) {
            throw new BusinessException("report error, executor is down");
        }

        return executorMonitorDo.lookupConfigSetting(executorId);
    }

    public List<ExecutorConfigBo> lookupExecutorConfigMonitor(Long serverId, String serverIp) {
        ExecutorMonitorDo executorMonitorDo = springDomainFactory.newExecutorMonitor(serverId, serverIp);

        boolean isRunning = executorMonitorDo.isRunning();
        if (!isRunning) {
            throw new BusinessException("report error, executor is down");
        }

        return executorMonitorDo.lookupConfigMonitor();
    }

    public ExecutorConfigBo lookupExecutorConfigMonitor(Long serverId, String serverIp, String executorId) {
        ExecutorMonitorDo executorMonitorDo = springDomainFactory.newExecutorMonitor(serverId, serverIp);

        boolean isRunning = executorMonitorDo.isRunning();
        if (!isRunning) {
            throw new BusinessException("report error, executor is down");
        }

        return executorMonitorDo.lookupConfigMonitor(executorId);
    }

    public ExecutorStatisticsBo lookupExecutorStatisticsMonitor(Long serverId, String serverIp, String executorId) {
        ExecutorMonitorDo executorMonitorDo = springDomainFactory.newExecutorMonitor(serverId, serverIp);

        boolean isRunning = executorMonitorDo.isRunning();
        if (!isRunning) {
            throw new BusinessException("report error, executor is down");
        }

        return executorMonitorDo.lookupStatistics(executorId);
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
