package com.share.co.kcl.dtp.core.reporter;

import com.share.co.kcl.dtp.common.utils.NetworkUtils;
import com.share.co.kcl.dtp.core.DynamicThreadPoolExecutor;
import com.share.co.kcl.dtp.core.monitor.ExecutorMonitor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public abstract class AbstractExecutorReporter<T> implements Reporter {

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
                    List<T> reportBodies = ExecutorMonitor.watch()
                            .entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByKey())
                            .map(entry -> {
                                String executorId = computeExecutorId(entry);
                                String executorName = computeExecutorName(entry);
                                ThreadPoolExecutor executorObject = computeExecutorObject(entry);
                                return buildReportBodies(executorId, executorName, executorObject);
                            }).collect(Collectors.toList());
                    AbstractExecutorReporter.this.sendReport(serverCode, serverSecret, serverIp, reportBodies);
                } catch (Exception ignore) {
                    // ignore any exception
                }
            }
        }, 1000, 5000);
    }

    private String computeExecutorId(Map.Entry<String, ThreadPoolExecutor> entry) {
        @SuppressWarnings("UnnecessaryLocalVariable")
        String executorId = entry.getKey();
        return executorId;
    }

    private String computeExecutorName(Map.Entry<String, ThreadPoolExecutor> entry) {
        String executorName = "";

        ThreadPoolExecutor threadPoolExecutor = this.computeExecutorObject(entry);
        if (threadPoolExecutor instanceof DynamicThreadPoolExecutor) {
            executorName = ((DynamicThreadPoolExecutor) threadPoolExecutor).getExecutorName();
        }

        return executorName;
    }

    private ThreadPoolExecutor computeExecutorObject(Map.Entry<String, ThreadPoolExecutor> entry) {
        @SuppressWarnings("UnnecessaryLocalVariable")
        ThreadPoolExecutor threadPoolExecutor = entry.getValue();
        return threadPoolExecutor;
    }

    /**
     * build the report body
     *
     * @param executorId         executor id
     * @param executorName       executor name
     * @param threadPoolExecutor executor object
     * @return report body
     */
    protected abstract T buildReportBodies(String executorId, String executorName, ThreadPoolExecutor threadPoolExecutor);

    /**
     * send the report body to remote
     *
     * @param serverCode   server code
     * @param serverSecret server secret
     * @param serverIp     server ip
     * @param reportBodies report bodies
     * @return success / false
     */
    protected abstract boolean sendReport(String serverCode, String serverSecret, String serverIp, List<T> reportBodies);
}
