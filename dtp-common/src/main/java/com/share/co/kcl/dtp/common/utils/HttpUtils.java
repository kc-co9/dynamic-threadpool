package com.share.co.kcl.dtp.common.utils;


import com.alibaba.fastjson2.JSON;
import com.share.co.kcl.dtp.common.exception.HttpException;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HttpUtils {

    private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);

    private static final Integer CONNECT_TIMEOUT = 30;
    private static final Integer READ_TIMEOUT = 10;

    private static final MediaType MEDIA_JSON = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            // 添加日志拦截器
            .addInterceptor(
                    new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();

    private HttpUtils() {
    }

    public static String doGet(String path, Map<String, String> headers, Map<String, String> params) {
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("url is null");
        }

        Headers.Builder headerBuilder = new Headers.Builder();
        headers.forEach(headerBuilder::add);
        Headers requestHeaders = headerBuilder.build();

        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(path)).newBuilder();
        if (MapUtils.isNotEmpty(params)) {
            params.forEach(httpBuilder::addQueryParameter);
        }
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .headers(requestHeaders)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                LOG.warn("http GET request return exception code:{},body:{}", response.code(), response.body());
                throw new HttpException("http GET request failure");
            }
            return response.body().string();
        } catch (IOException e) {
            LOG.error("http GET request throw IOException", e);
            throw new HttpException("http GET request failure");
        }
    }

    public static String doPost(String path, Map<String, String> headers, Object body) {
        Headers.Builder headerBuilder = new Headers.Builder();
        headers.forEach(headerBuilder::add);
        Headers requestHeaders = headerBuilder.build();
        RequestBody requestBody = RequestBody.create(JSON.toJSONString(body), MEDIA_JSON);
        Request request = new Request.Builder()
                .url(path)
                .headers(requestHeaders)
                .post(requestBody)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                LOG.warn("http POST request return exception code:{},body:{}", response.code(), response.body());
                throw new HttpException("http POST request failure");
            }
            return response.body().string();
        } catch (IOException e) {
            LOG.error("http POST request throw IOException", e);
            throw new HttpException("http POST request failure");
        }
    }
}
