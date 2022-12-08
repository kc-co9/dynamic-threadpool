package com.share.co.kcl.dtp.core.reporter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.share.co.kcl.dtp.common.constants.ResultCode;
import com.share.co.kcl.dtp.common.enums.RejectedStrategy;
import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.common.model.dto.ExecutorConfigReportDto;
import com.share.co.kcl.dtp.common.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultExecutorConfigReporter extends AbstractExecutorReporter<ExecutorConfigReportDto> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultExecutorConfigReporter.class);

    private final String reportLink;

    public DefaultExecutorConfigReporter(String serverCode, String serverSecret, String reportLink) {
        super(serverCode, serverSecret);
        this.reportLink = reportLink;
    }

    @Override
    protected ExecutorConfigReportDto buildReportBodies(String executorId, String executorName, ThreadPoolExecutor threadPoolExecutor) {
        ExecutorConfigBo executorConfigBo = new ExecutorConfigBo();
        executorConfigBo.setExecutorId(executorId);
        executorConfigBo.setExecutorName(executorName);
        executorConfigBo.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        executorConfigBo.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
        executorConfigBo.setKeepAliveTime(threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        executorConfigBo.setRejectedStrategy(
                RejectedStrategy.parse(threadPoolExecutor.getRejectedExecutionHandler()));
        return new ExecutorConfigReportDto(executorId, executorName, executorConfigBo);
    }

    @Override
    protected boolean sendReport(String serverCode, String serverSecret, String serverIp, List<ExecutorConfigReportDto> executorList) {
        Map<String, String> headers = new HashMap<>();
        headers.put("secret", serverSecret);

        Map<String, Object> body = new HashMap<>();
        body.put("serverCode", serverCode);
        body.put("serverIp", serverIp);
        body.put("executorConfigList", executorList);

        String result = HttpUtils.doPost(reportLink, headers, body);
        JSONObject response = JSON.parseObject(result);
        Integer responseCode = response.getInteger("code");
        String responseMsg = response.getString("msg");
        if (!ResultCode.SUCCESS.getCode().equals(responseCode)) {
            LOG.error("executor reporter request remote server failure, error msg: {}", responseMsg);
            return false;
        }
        return true;
    }

    @Override
    protected long reportDelay() {
        return 3000L;
    }

    @Override
    protected long reportPeriod() {
        return 3000L;
    }
}
