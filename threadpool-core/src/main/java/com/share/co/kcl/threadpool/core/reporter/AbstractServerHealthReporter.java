package com.share.co.kcl.threadpool.core.reporter;

import com.share.co.kcl.common.utils.AddressUtils;
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
    protected String serverIp;

    protected AbstractServerHealthReporter(String serverCode) {
        this.serverCode = serverCode;
        this.serverIp = AddressUtils.getLocalIpList().get(0);
    }

    @Override
    public void report() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    AbstractServerHealthReporter.this.sendReport(serverCode, serverIp);
                } catch (Exception ignore) {
                }
            }
        }, 3000, 3000);
    }

    /**
     * send the report body to remote
     *
     * @param reportBody report bodies
     * @return success / false
     */
    protected abstract boolean sendReport(String serverCode, String serverIp);
}
