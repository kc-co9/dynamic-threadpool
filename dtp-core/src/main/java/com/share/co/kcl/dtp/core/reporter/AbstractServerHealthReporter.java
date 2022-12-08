package com.share.co.kcl.dtp.core.reporter;

import com.share.co.kcl.dtp.common.utils.NetworkUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractServerHealthReporter implements Reporter {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractServerHealthReporter.class);

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

    protected AbstractServerHealthReporter(String serverCode, String serverSecret) {
        this.serverCode = serverCode;
        this.serverSecret = serverSecret;
        this.serverIp = NetworkUtils.getLocalIpList().get(0);
    }

    @Override
    public void report() {
        reporterThreadPoolExecutor.scheduleWithFixedDelay(() -> {
            try {
                sendReport(serverCode, serverSecret, serverIp);
            } catch (Exception ex) {
                // ignore any exception
                LOG.debug("report server throw exception", ex);
            }
        }, this.reportDelay(), this.reportPeriod(), TimeUnit.MILLISECONDS);
    }

    /**
     * send the report body to remote
     *
     * @param serverCode server code
     * @param serverIp   server ip
     * @return success / false
     */
    protected abstract boolean sendReport(String serverCode, String serverSecret, String serverIp);

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
