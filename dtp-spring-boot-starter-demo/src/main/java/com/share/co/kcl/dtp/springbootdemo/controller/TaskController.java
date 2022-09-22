package com.share.co.kcl.dtp.springbootdemo.controller;

import com.share.co.kcl.dtp.core.DynamicThreadPoolExecutor;
import com.share.co.kcl.dtp.core.monitor.ExecutorMonitor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = "任务路由")
@RequestMapping(value = "/task")
public class TaskController {

    private static final DynamicThreadPoolExecutor dynamicPoolExecutor =
            new DynamicThreadPoolExecutor(10, 100, 3, TimeUnit.MINUTES, new LinkedBlockingQueue<>(100));
    private static final ThreadPoolExecutor poolExecutor =
            new ThreadPoolExecutor(10, 100, 3, TimeUnit.MINUTES, new LinkedBlockingQueue<>(100));

    static {
        // set dynamicPoolExecutor name
        dynamicPoolExecutor.setExecutorName("spring boot demo dynamic thread pool executor");

        // register poolExecutor
        ExecutorMonitor.register(poolExecutor);
    }

    @Autowired
    private ThreadPoolExecutor dynamicPoolExecutorBean;
    @Autowired
    private ThreadPoolExecutor jdkPoolExecutorBean;

    @ApiOperation(value = "提交动态线程池")
    @PostMapping(value = "/v1/submitDynamicPoolExecutor")
    public void submitDynamicPoolExecutor() {
        dynamicPoolExecutor.execute(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ignore) {
                }
            }
        });
    }

    @ApiOperation(value = "提交JDK线程池")
    @PostMapping(value = "/v1/submitJdkPoolExecutor")
    public void submitJdkPoolExecutor() {
        dynamicPoolExecutor.execute(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ignore) {
                }
            }
        });
    }

    @ApiOperation(value = "提交动态线程池（Bean）")
    @PostMapping(value = "/v1/submitDynamicPoolExecutorBean")
    public void submitDynamicPoolExecutorBean() {
        dynamicPoolExecutorBean.execute(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ignore) {
                }
            }
        });
    }

    @ApiOperation(value = "提交JDK线程池（Bean）")
    @PostMapping(value = "/v1/submitJdkPoolExecutorBean")
    public void submitJdkPoolExecutorBean() {
        jdkPoolExecutorBean.execute(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ignore) {
                }
            }
        });
    }
}
