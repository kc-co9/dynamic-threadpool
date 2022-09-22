package com.share.co.kcl.dtp.spring.meta;

public interface DynamicPoolConfigMeta {

    String SERVER_CODE = "dtp.server-code";
    String SERVER_MONITOR = "dtp.server-monitor";

    String SERVER_REPORT_HEALTH_URL = "/monitor/v1/reportServerHealth";
    String EXECUTOR_REPORT_INFO_URL = "/monitor/v1/reportExecutorInfo";

    String EXECUTOR_REFRESH_CHECK_SYNC_URL = "/monitor/v1/checkExecutorSync";
    String EXECUTOR_REFRESH_PULL_SYNC_URL = "/monitor/v1/lookupExecutorSync";

}
