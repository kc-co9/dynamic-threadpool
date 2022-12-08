package com.share.co.kcl.dtp.springbootdemo.config;

import com.share.co.kcl.dtp.core.DynamicThreadPoolExecutor;
import com.share.co.kcl.dtp.core.monitor.ExecutorMonitor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor dynamicPoolExecutorBean() {
        DynamicThreadPoolExecutor dynamicThreadPoolExecutor =
                new DynamicThreadPoolExecutor(10, 20, 3, TimeUnit.MINUTES, new LinkedBlockingQueue<>(10));
        dynamicThreadPoolExecutor.setExecutorName("spring boot demo dynamic thread pool executor bean");
        return dynamicThreadPoolExecutor;
    }

    @Bean
    public ThreadPoolExecutor jdkPoolExecutorBean() {
        ThreadPoolExecutor jdkThreadPoolExecutor = new ThreadPoolExecutor(10, 20, 3, TimeUnit.MINUTES, new LinkedBlockingQueue<>(10));
        // register poolExecutor
        ExecutorMonitor.register(jdkThreadPoolExecutor);
        return jdkThreadPoolExecutor;
    }
}
