package com.share.co.kcl.dtp.springbootdemo.controller;

import com.share.co.kcl.dtp.core.DynamicThreadPoolExecutor;
import com.share.co.kcl.dtp.core.monitor.ExecutorMonitor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = "任务路由")
@RequestMapping(value = "/task")
public class TaskController {

    private static final DynamicThreadPoolExecutor dynamicPoolExecutor =
            new DynamicThreadPoolExecutor(10, 20, 3, TimeUnit.MINUTES, new LinkedBlockingQueue<>(10));
    private static final ThreadPoolExecutor jdkPoolExecutor =
            new ThreadPoolExecutor(10, 20, 3, TimeUnit.MINUTES, new LinkedBlockingQueue<>(10));

    static {
        // set dynamicPoolExecutor name
        dynamicPoolExecutor.setExecutorName("spring boot demo dynamic thread pool executor");

        // register poolExecutor
        ExecutorMonitor.register(jdkPoolExecutor);
    }

    @Autowired
    private ThreadPoolExecutor dynamicPoolExecutorBean;
    @Autowired
    private ThreadPoolExecutor jdkPoolExecutorBean;

    @ApiOperation(value = "提交动态线程池")
    @PostMapping(value = "/v1/submitDynamicPoolExecutor")
    public Map<String, Object> submitDynamicPoolExecutor() {
        dynamicPoolExecutor.execute(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ignore) {
                }
            }
        });
        return this.exportExecutorConfig(dynamicPoolExecutor);
    }

    @ApiOperation(value = "提交JDK线程池")
    @PostMapping(value = "/v1/submitJdkPoolExecutor")
    public Map<String, Object> submitJdkPoolExecutor() {
        jdkPoolExecutor.execute(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ignore) {
                }
            }
        });
        return this.exportExecutorConfig(jdkPoolExecutor);
    }

    @ApiOperation(value = "提交动态线程池（Bean）")
    @PostMapping(value = "/v1/submitDynamicPoolExecutorBean")
    public Map<String, Object> submitDynamicPoolExecutorBean() {
        dynamicPoolExecutorBean.execute(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ignore) {
                }
            }
        });
        return this.exportExecutorConfig(dynamicPoolExecutor);
    }

    @ApiOperation(value = "提交JDK线程池（Bean）")
    @PostMapping(value = "/v1/submitJdkPoolExecutorBean")
    public Map<String, Object> submitJdkPoolExecutorBean() {
        jdkPoolExecutorBean.execute(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ignore) {
                }
            }
        });
        return this.exportExecutorConfig(jdkPoolExecutorBean);
    }

    private Map<String, Object> exportExecutorConfig(ThreadPoolExecutor executor) {
        Map<String, Object> result = new HashMap<>();
        result.put("CorePoolSize", executor.getCorePoolSize());
        result.put("MaximumPoolSize", executor.getMaximumPoolSize());
        result.put("RejectedExecutionHandler", executor.getRejectedExecutionHandler().getClass().getSimpleName());
        result.put("KeepAliveTime", executor.getKeepAliveTime(TimeUnit.SECONDS));
        return result;
    }
}
