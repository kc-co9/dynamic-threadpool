package com.share.co.kcl.dtp.springdemo.config;

import com.share.co.kcl.dtp.core.DynamicThreadPoolExecutor;
import com.share.co.kcl.dtp.core.monitor.ExecutorMonitor;
import com.share.co.kcl.dtp.spring.EnableDynamicPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableDynamicPool
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor dynamicPoolExecutorBean() {
        DynamicThreadPoolExecutor dynamicThreadPoolExecutor =
                new DynamicThreadPoolExecutor(10, 100, 3, TimeUnit.MINUTES, new LinkedBlockingQueue<>(100));
        dynamicThreadPoolExecutor.setExecutorName("spring demo dynamic thread pool executor bean");
        return dynamicThreadPoolExecutor;
    }

    @Bean
    public ThreadPoolExecutor jdkPoolExecutorBean() {
        ThreadPoolExecutor jdkThreadPoolExecutor = new ThreadPoolExecutor(10, 100, 3, TimeUnit.MINUTES, new LinkedBlockingQueue<>(100));
        // register poolExecutor
        ExecutorMonitor.register(jdkThreadPoolExecutor);
        return jdkThreadPoolExecutor;
    }
}
