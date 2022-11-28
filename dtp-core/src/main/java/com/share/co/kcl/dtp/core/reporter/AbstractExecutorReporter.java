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
import java.util.concurrent.ThreadPoolExecutor;
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
                                ThreadPoolExecutor threadPoolExecutor = entry.getValue();

                                String executorName = "";
                                if (threadPoolExecutor instanceof DynamicThreadPoolExecutor) {
                                    executorName = ((DynamicThreadPoolExecutor) threadPoolExecutor).getExecutorName();
                                }

                                ExecutorConfigBo executorConfigBo = new ExecutorConfigBo();
                                executorConfigBo.setExecutorId(executorId);
                                executorConfigBo.setExecutorName(executorName);
                                executorConfigBo.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
                                executorConfigBo.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
                                executorConfigBo.setKeepAliveTime(threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
                                executorConfigBo.setRejectedStrategy(
                                        RejectedStrategy.parse(threadPoolExecutor.getRejectedExecutionHandler()));

                                ExecutorStatisticsBo executorStatisticsBo = new ExecutorStatisticsBo();
                                executorStatisticsBo.setExecutorId(executorId);
                                executorStatisticsBo.setExecutorName(executorName);
                                executorStatisticsBo.setQueueClass(threadPoolExecutor.getQueue().getClass().getSimpleName());
                                executorStatisticsBo.setQueueNodeCount(threadPoolExecutor.getQueue().size());
                                executorStatisticsBo.setQueueRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());
                                executorStatisticsBo.setPoolSize(threadPoolExecutor.getPoolSize());
                                executorStatisticsBo.setLargestPoolSize(threadPoolExecutor.getLargestPoolSize());
                                executorStatisticsBo.setActiveCount(threadPoolExecutor.getActiveCount());
                                executorStatisticsBo.setTaskCount(threadPoolExecutor.getTaskCount());
                                executorStatisticsBo.setCompletedTaskCount(threadPoolExecutor.getCompletedTaskCount());

                                ExecutorReportDto executorReportDto = new ExecutorReportDto();
                                executorReportDto.setExecutorId(executorId);
                                executorReportDto.setExecutorName(executorName);
                                executorReportDto.setConfigBody(executorConfigBo);
                                executorReportDto.setStatisticsBody(executorStatisticsBo);

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
