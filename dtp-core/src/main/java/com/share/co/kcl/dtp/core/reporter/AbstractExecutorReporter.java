package com.share.co.kcl.dtp.core.reporter;

import com.share.co.kcl.dtp.common.enums.RejectedStrategy;
import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.common.model.dto.ExecutorReportDto;
import com.share.co.kcl.dtp.common.model.bo.ExecutorStatisticsBo;
import com.share.co.kcl.dtp.common.utils.NetworkUtils;
import com.share.co.kcl.dtp.core.DynamicThreadPoolExecutor;
import com.share.co.kcl.dtp.core.monitor.ExecutorMonitor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class AbstractExecutorReporter implements Reporter {

    @Getter
    @Setter
    protected String serverCode;
    @Getter
    @Setter
    protected String serverSecret;
    @Getter
    @Setter
    protected String serverIp;

    protected AbstractExecutorReporter(String serverCode, String serverSecret) {
        this.serverCode = serverCode;
        this.serverSecret = serverSecret;
        this.serverIp = NetworkUtils.getLocalIpList().get(0);
    }

    @Override
    public void report() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    List<ExecutorReportDto> reportBodies = ExecutorMonitor.watch()
                            .entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByKey())
                            .map(entry -> {

                                String executorId = entry.getKey();
                                String executorName = "";
                                if (entry.getValue() instanceof DynamicThreadPoolExecutor) {
                                    executorName = ((DynamicThreadPoolExecutor) entry.getValue()).getExecutorName();
                                }

                                ExecutorConfigBo configBody = new ExecutorConfigBo();
                                configBody.setExecutorId(executorId);
                                configBody.setExecutorName(executorName);
                                configBody.setCorePoolSize(entry.getValue().getCorePoolSize());
                                configBody.setMaximumPoolSize(entry.getValue().getMaximumPoolSize());
                                configBody.setKeepAliveTime(entry.getValue().getKeepAliveTime(TimeUnit.SECONDS));
                                configBody.setRejectedStrategy(RejectedStrategy.parse(entry.getValue().getRejectedExecutionHandler()));

                                ExecutorStatisticsBo statisticsBody = new ExecutorStatisticsBo();
                                statisticsBody.setExecutorId(executorId);
                                statisticsBody.setExecutorName(executorName);
                                statisticsBody.setPoolSize(entry.getValue().getPoolSize());
                                statisticsBody.setLargestPoolSize(entry.getValue().getLargestPoolSize());
                                statisticsBody.setActiveCount(entry.getValue().getActiveCount());
                                statisticsBody.setTaskCount(entry.getValue().getTaskCount());
                                statisticsBody.setCompletedTaskCount(entry.getValue().getCompletedTaskCount());

                                ExecutorReportDto executorReportDto = new ExecutorReportDto();
                                executorReportDto.setExecutorId(executorId);
                                executorReportDto.setExecutorName(executorName);
                                executorReportDto.setConfigBody(configBody);
                                executorReportDto.setStatisticsBody(statisticsBody);

                                return executorReportDto;
                            }).collect(Collectors.toList());
                    AbstractExecutorReporter.this.sendReport(serverCode, serverSecret, serverIp, reportBodies);
                } catch (Exception ignore) {
                    // ignore any exception
                }
            }
        }, 1000, 5000);
    }

    /**
     * send the report body to remote
     *
     * @param serverCode   server code
     * @param serverSecret server secret
     * @param serverIp     server ip
     * @param reportBodies report bodies
     * @return success / false
     */
    protected abstract boolean sendReport(String serverCode, String serverSecret, String serverIp, List<ExecutorReportDto> reportBodies);
}
