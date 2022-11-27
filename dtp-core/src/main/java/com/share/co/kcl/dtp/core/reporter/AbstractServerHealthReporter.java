package com.share.co.kcl.dtp.core.reporter;

import com.share.co.kcl.dtp.common.utils.NetworkUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractServerHealthReporter implements Reporter {

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
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    AbstractServerHealthReporter.this.sendReport(serverCode, serverSecret, serverIp);
                } catch (Exception ignore) {
                    // ignore any exception
                }
            }
        }, 0, 3000);
    }

    /**
     * send the report body to remote
     *
     * @param serverCode server code
     * @param serverIp   server ip
     * @return success / false
     */
    protected abstract boolean sendReport(String serverCode, String serverSecret, String serverIp);
}
