package com.share.co.kcl.threadpool.core.refresher;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.share.co.kcl.common.constants.ResultCode;
import com.share.co.kcl.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.common.utils.HttpUtils;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class DefaultExecutorRefresher extends AbstractExecutorRefresher {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultExecutorRefresher.class);

    private final String checkRefreshLink;
    private final String pullRefreshLink;

    public DefaultExecutorRefresher(String serverCode, String checkExecutorSyncLink, String pullExecutorSyncLink) {
        super(serverCode);
        this.checkRefreshLink = checkExecutorSyncLink;
        this.pullRefreshLink = pullExecutorSyncLink;
    }

    @Override
    protected boolean checkExecutorSync(String serverCode, String serverIp) {
        Map<String, String> params = new HashMap<>();
        params.put("serverCode", serverCode);
        params.put("serverIp", serverIp);
        ResponseBody responseBody = HttpUtils.doGet(checkRefreshLink, params);
        try {
            JSONObject response = JSON.parseObject(responseBody.string());

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
            return Boolean.TRUE.equals(responseData.getBoolean("isSync"));
        } catch (IOException e) {
            LOG.error("http response parse error", e);
            return false;
        }
    }

    @Override
    protected List<ExecutorConfigBo> pullExecutorSync(String serverCode, String serverIp) {
        Map<String, String> params = new HashMap<>();
        params.put("serverCode", serverCode);
        params.put("serverIp", serverIp);
        ResponseBody responseBody = HttpUtils.doGet(pullRefreshLink, params);
        try {
            JSONObject response = JSON.parseObject(responseBody.string());

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
        } catch (IOException e) {
            LOG.error("http response parse error", e);
            return Collections.emptyList();
        }
    }
}
