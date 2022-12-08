package com.share.co.kcl.dtp.core.reporter;

import com.share.co.kcl.dtp.common.utils.NetworkUtils;
import com.share.co.kcl.dtp.core.DynamicThreadPoolExecutor;
import com.share.co.kcl.dtp.core.monitor.ExecutorMonitor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class AbstractExecutorReporter<T> implements Reporter {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractExecutorReporter.class);

    private final ScheduledExecutorService reporterThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

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
        reporterThreadPoolExecutor.scheduleWithFixedDelay(() -> {
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
            } catch (Exception ex) {
                // ignore any exception
                LOG.debug("report executor throw exception", ex);
            }
        }, this.reportDelay(), this.reportPeriod(), TimeUnit.MILLISECONDS);
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

    /**
     * the time to delay first report
     *
     * @return milliseconds
     */
    protected abstract long reportDelay();

    /**
     * the period between the termination of one report and the commencement of the next
     *
     * @return milliseconds
     */
    protected abstract long reportPeriod();
}
