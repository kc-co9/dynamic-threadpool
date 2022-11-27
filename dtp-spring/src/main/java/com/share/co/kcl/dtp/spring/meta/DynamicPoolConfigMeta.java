package com.share.co.kcl.dtp.spring.meta;

public interface DynamicPoolConfigMeta {

    String SERVER_CODE = "dtp.server-code";
    String SERVER_SECRET = "dtp.server-secret";
    String SERVER_DOMAIN = "dtp.server-domain";

    String SERVER_REPORT_HEALTH_URL = "/monitor/server/v1/reportServerHealth";
    String EXECUTOR_REPORT_INFO_URL = "/monitor/executor/v1/reportExecutorInfo";

    String EXECUTOR_REFRESH_CHECK_SYNC_URL = "/monitor/executor/v1/checkExecutorSync";
    String EXECUTOR_REFRESH_PULL_SYNC_URL = "/monitor/executor/v1/lookupExecutorSync";

}
