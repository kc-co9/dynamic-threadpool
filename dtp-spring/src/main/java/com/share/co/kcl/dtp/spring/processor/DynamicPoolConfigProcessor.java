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
        String serverSecret = poolConfig.getServerSecret();
        String serverDomain = poolConfig.getServerDomain();

        this.startServerReporter(serverCode, serverSecret, serverDomain);
        this.startExecutorReporter(serverCode, serverSecret, serverDomain);
        this.startExecutorRefresher(serverCode, serverSecret, serverDomain);
    }

    private void startServerReporter(String serverCode, String serverSecret, String serverDomain) {
        String reportLink = serverDomain + DynamicPoolConfigMeta.SERVER_REPORT_HEALTH_URL;
        new DefaultServerHealthReporter(serverCode, serverSecret, reportLink).report();
    }

    private void startExecutorReporter(String serverCode, String serverSecret, String serverDomain) {
        String reportLink = serverDomain + DynamicPoolConfigMeta.EXECUTOR_REPORT_INFO_URL;
        new DefaultExecutorReporter(serverCode, serverSecret, reportLink).report();
    }

    private void startExecutorRefresher(String serverCode, String serverSecret, String serverDomain) {
        String checkSyncLink = serverDomain + EXECUTOR_REFRESH_CHECK_SYNC_URL;
        String pullSyncLink = serverDomain + EXECUTOR_REFRESH_PULL_SYNC_URL;
        new DefaultExecutorRefresher(serverCode, serverSecret, checkSyncLink, pullSyncLink).refresh();
    }
}
