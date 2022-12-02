package com.share.co.kcl.dtp.spring.meta;

public interface DynamicPoolConfigMeta {

    String SERVER_CODE = "dtp.server-code";
    String SERVER_SECRET = "dtp.server-secret";
    String SERVER_DOMAIN = "dtp.server-domain";

    String SERVER_REPORT_HEALTH_URL = "/monitor/server/v1/reportServerHealth";
    String EXECUTOR_CONFIG_REPORT_URL = "/monitor/executor/v1/reportExecutorConfig";
    String EXECUTOR_STATISTICS_REPORT_URL = "/monitor/executor/v1/reportExecutorStatistics";
    String EXECUTOR_STATUS_REPORT_URL = "/monitor/executor/v1/reportExecutorStatus";

    String EXECUTOR_UPDATE_CHECK_URL = "/monitor/executor/v1/checkExecutorUpdate";
    String EXECUTOR_UPDATE_FETCH_URL = "/monitor/executor/v1/lookupExecutorUpdate";

}
