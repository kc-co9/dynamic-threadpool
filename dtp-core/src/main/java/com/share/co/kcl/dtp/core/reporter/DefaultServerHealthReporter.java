package com.share.co.kcl.dtp.core.reporter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.share.co.kcl.dtp.common.constants.ResultCode;
import com.share.co.kcl.dtp.common.utils.HttpUtils;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DefaultServerHealthReporter extends AbstractServerHealthReporter {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultServerHealthReporter.class);

    private final String reportLink;

    public DefaultServerHealthReporter(String serverCode, String reportLink) {
        super(serverCode);
        this.reportLink = reportLink;
    }

    @Override
    protected boolean sendReport(String serverCode, String serverIp) {
        Map<String, String> params = new HashMap<>();
        params.put("serverCode", serverCode);
        params.put("serverIp", serverIp);
        ResponseBody responseBody = HttpUtils.doGet(reportLink, params);
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
