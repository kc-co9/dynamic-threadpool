package com.share.co.kcl.dtp.core.reporter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.share.co.kcl.dtp.common.constants.ResultCode;
import com.share.co.kcl.dtp.common.model.bo.ExecutorStatisticsBo;
import com.share.co.kcl.dtp.common.model.dto.ExecutorStatisticsReportDto;
import com.share.co.kcl.dtp.common.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class DefaultExecutorStatisticsReporter extends AbstractExecutorReporter<ExecutorStatisticsReportDto> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultExecutorStatisticsReporter.class);

    private final String reportLink;

    public DefaultExecutorStatisticsReporter(String serverCode, String serverSecret, String reportLink) {
        super(serverCode, serverSecret);
        this.reportLink = reportLink;
    }

    @Override
    protected ExecutorStatisticsReportDto buildReportBodies(String executorId, String executorName, ThreadPoolExecutor threadPoolExecutor) {
        ExecutorStatisticsBo executorStatisticsBo = new ExecutorStatisticsBo();
        executorStatisticsBo.setExecutorId(executorId);
        executorStatisticsBo.setExecutorName(executorName);
        executorStatisticsBo.setQueueClass(threadPoolExecutor.getQueue().getClass().getSimpleName());
        executorStatisticsBo.setQueueNodeCount(threadPoolExecutor.getQueue().size());
        executorStatisticsBo.setQueueRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());
        executorStatisticsBo.setPoolSize(threadPoolExecutor.getPoolSize());
        executorStatisticsBo.setLargestPoolSize(threadPoolExecutor.getLargestPoolSize());
        executorStatisticsBo.setActiveCount(threadPoolExecutor.getActiveCount());
        executorStatisticsBo.setTaskCount(threadPoolExecutor.getTaskCount());
        executorStatisticsBo.setCompletedTaskCount(threadPoolExecutor.getCompletedTaskCount());
        return new ExecutorStatisticsReportDto(executorId, executorName, executorStatisticsBo);
    }

    @Override
    protected boolean sendReport(String serverCode, String serverSecret, String serverIp, List<ExecutorStatisticsReportDto> executorList) {
        Map<String, String> headers = new HashMap<>();
        headers.put("secret", serverSecret);

        Map<String, Object> body = new HashMap<>();
        body.put("serverCode", serverCode);
        body.put("serverIp", serverIp);
        body.put("executorStatisticsList", executorList);

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
        return 5000L;
    }

    @Override
    protected long reportPeriod() {
        return 5000L;
    }
}
