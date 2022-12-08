package com.share.co.kcl.dtp.core.refresher;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.share.co.kcl.dtp.common.constants.ResultCode;
import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.common.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DefaultExecutorRefresher extends AbstractExecutorRefresher {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultExecutorRefresher.class);

    private final String checkRefreshLink;
    private final String fetchRefreshLink;

    public DefaultExecutorRefresher(String serverCode, String serverSecret,
                                    String checkExecutorLink, String fetchExecutorLink) {
        super(serverCode, serverSecret);
        this.checkRefreshLink = checkExecutorLink;
        this.fetchRefreshLink = fetchExecutorLink;
    }

    @Override
    protected boolean checkUpdate(String serverCode, String serverIp) {
        Map<String, String> headers = new HashMap<>();
        headers.put("secret", serverSecret);

        Map<String, String> params = new HashMap<>();
        params.put("serverCode", serverCode);
        params.put("serverIp", serverIp);

        String result = HttpUtils.doGet(checkRefreshLink, headers, params);
        JSONObject response = JSON.parseObject(result);

        int responseCode = response.getInteger("code");
        String responseMsg = response.getString("msg");
        JSONObject responseData = response.getJSONObject("data");

        if (!ResultCode.SUCCESS.getCode().equals(responseCode)) {
            LOG.error("executor reporter request remote server failure, error msg: {}", responseMsg);
            return false;
        }
        if (Objects.isNull(responseData)) {
            return false;
        }
        return Boolean.TRUE.equals(responseData.getBoolean("isNeedToSync"));
    }

    @Override
    protected List<ExecutorConfigBo> fetchUpdate(String serverCode, String serverIp) {
        Map<String, String> headers = new HashMap<>();
        headers.put("secret", serverSecret);

        Map<String, String> params = new HashMap<>();
        params.put("serverCode", serverCode);
        params.put("serverIp", serverIp);

        String result = HttpUtils.doGet(fetchRefreshLink, headers, params);
        JSONObject response = JSON.parseObject(result);

        int responseCode = response.getInteger("code");
        String responseMsg = response.getString("msg");
        JSONObject responseData = response.getJSONObject("data");

        if (!ResultCode.SUCCESS.getCode().equals(responseCode)) {
            LOG.error("executor reporter request remote server failure, error msg: {}", responseMsg);
            return Collections.emptyList();
        }
        if (Objects.isNull(responseData)) {
            return Collections.emptyList();
        }
        List<ExecutorConfigBo> executorConfigList = responseData.getObject("executorConfigList", new TypeReference<List<ExecutorConfigBo>>() {
        });
        return Optional.ofNullable(executorConfigList).orElse(Collections.emptyList());

    }
}
