package com.share.co.kcl.dtp.core.reporter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.share.co.kcl.dtp.common.constants.ResultCode;
import com.share.co.kcl.dtp.common.model.dto.ExecutorReportDto;
import com.share.co.kcl.dtp.common.utils.HttpUtils;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultExecutorReporter extends AbstractExecutorReporter {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultExecutorReporter.class);

    private final String reportLink;

    public DefaultExecutorReporter(String serverCode, String reportLink) {
        super(serverCode);
        this.reportLink = reportLink;
    }

    @Override
    protected boolean sendReport(String serverCode, String serverIp, List<ExecutorReportDto> executorList) {
        Map<String, Object> body = new HashMap<>();
        body.put("serverCode", serverCode);
        body.put("serverIp", serverIp);
        body.put("executorList", executorList);
        ResponseBody responseBody = HttpUtils.doPost(reportLink, body);
        try {
            JSONObject response = JSON.parseObject(responseBody.string());
            Integer responseCode = response.getInteger("code");
            String responseMsg = response.getString("msg");
            if (!ResultCode.SUCCESS.getCode().equals(responseCode)) {
                LOG.error("executor reporter request remote server failure, error msg: {}", responseMsg);
                return false;
            }
            return true;
        } catch (IOException e) {
            LOG.error("executor reporter parses response failure", e);
            return false;
        }
    }
}
