package com.share.co.kcl.dtp.spring.processor;

import com.share.co.kcl.dtp.core.refresher.DefaultExecutorRefresher;
import com.share.co.kcl.dtp.core.reporter.DefaultExecutorReporter;
import com.share.co.kcl.dtp.core.reporter.DefaultServerHealthReporter;
import com.share.co.kcl.dtp.spring.meta.DynamicPoolConfig;
import com.share.co.kcl.dtp.spring.meta.DynamicPoolConfigMeta;
import com.share.co.kcl.dtp.spring.meta.DynamicPoolConfigRepository;
import org.springframework.beans.factory.InitializingBean;

import static com.share.co.kcl.dtp.spring.meta.DynamicPoolConfigMeta.*;

public class DynamicPoolConfigProcessor implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        DynamicPoolConfig poolConfig = DynamicPoolConfigRepository.get();

        String serverCode = poolConfig.getServerCode();
        String serverMonitor = poolConfig.getServerMonitor();

        this.startServerReporter(serverCode, serverMonitor);
        this.startExecutorReporter(serverCode, serverMonitor);
        this.startExecutorRefresher(serverCode, serverMonitor);
    }

    private void startServerReporter(String serverCode, String serverMonitor) {
        String reportLink = serverMonitor + DynamicPoolConfigMeta.SERVER_REPORT_HEALTH_URL;
        new DefaultServerHealthReporter(serverCode, reportLink).report();
    }

    private void startExecutorReporter(String serverCode, String serverMonitor) {
        String reportLink = serverMonitor + DynamicPoolConfigMeta.EXECUTOR_REPORT_INFO_URL;
        new DefaultExecutorReporter(serverCode, reportLink).report();
    }

    private void startExecutorRefresher(String serverCode, String serverMonitor) {
        String checkSyncLink = serverMonitor + EXECUTOR_REFRESH_CHECK_SYNC_URL;
        String pullSyncLink = serverMonitor + EXECUTOR_REFRESH_PULL_SYNC_URL;
        new DefaultExecutorRefresher(serverCode, checkSyncLink, pullSyncLink).refresh();
    }
}
