package com.share.co.kcl.threadpool.core.monitor;

import com.share.co.kcl.common.generator.StringGenerator;
import com.share.co.kcl.threadpool.core.DynamicThreadPoolExecutor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class ExecutorMonitor {

    private static final Map<String, ThreadPoolExecutor> THREAD_POOL_EXECUTOR_MAP = new ConcurrentHashMap<>();

    private ExecutorMonitor() {
    }

    public static void register(ThreadPoolExecutor executor) {
        if (executor instanceof DynamicThreadPoolExecutor) {
            THREAD_POOL_EXECUTOR_MAP.put(((DynamicThreadPoolExecutor) executor).getExecutorId(), executor);
        } else {
            THREAD_POOL_EXECUTOR_MAP.put(new StringGenerator.UUIDGenerator().next(), executor);
        }
    }

    public static Map<String, ThreadPoolExecutor> watch() {
        return Collections.unmodifiableMap(THREAD_POOL_EXECUTOR_MAP);
    }

    public static ThreadPoolExecutor offline(String threadPoolId) {
        return THREAD_POOL_EXECUTOR_MAP.remove(threadPoolId);
    }

    public static Map<String, ThreadPoolExecutor> clear() {
        Map<String, ThreadPoolExecutor> copy = new HashMap<>(THREAD_POOL_EXECUTOR_MAP);
        THREAD_POOL_EXECUTOR_MAP.clear();
        return copy;
    }
}
