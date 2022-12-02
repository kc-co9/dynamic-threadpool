package com.share.co.kcl.dtp.spring.processor;

import com.share.co.kcl.dtp.core.refresher.DefaultExecutorRefresher;
import com.share.co.kcl.dtp.core.reporter.DefaultExecutorConfigReporter;
import com.share.co.kcl.dtp.core.reporter.DefaultExecutorStatisticsReporter;
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
        String configReportLink = serverDomain + DynamicPoolConfigMeta.EXECUTOR_CONFIG_REPORT_URL;
        String statisticsReportLink = serverDomain + DynamicPoolConfigMeta.EXECUTOR_STATISTICS_REPORT_URL;
        new DefaultExecutorConfigReporter(serverCode, serverSecret, configReportLink).report();
        new DefaultExecutorStatisticsReporter(serverCode, serverSecret, statisticsReportLink).report();
    }

    private void startExecutorRefresher(String serverCode, String serverSecret, String serverDomain) {
        String checkUpdateLink = serverDomain + EXECUTOR_UPDATE_CHECK_URL;
        String fetchUpdateLink = serverDomain + EXECUTOR_UPDATE_FETCH_URL;
        new DefaultExecutorRefresher(serverCode, serverSecret, checkUpdateLink, fetchUpdateLink).refresh();
    }
}
