package com.share.co.kcl.threadpool.core;

import com.share.co.kcl.common.generator.StringGenerator;
import com.share.co.kcl.threadpool.core.monitor.ExecutorMonitor;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicThreadPoolExecutor extends ThreadPoolExecutor {

    private static final AtomicInteger threadPoolNumber = new AtomicInteger(1);

    /**
     * thread pool id
     */
    @Getter
    @Setter
    private String executorId;

    @Getter
    @Setter
    private String executorName;

    public DynamicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), new AbortPolicy());
    }

    public DynamicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new AbortPolicy());
    }

    public DynamicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), handler);
    }

    public DynamicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, String executorName) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), new AbortPolicy(), executorName);
    }

    public DynamicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler, DynamicThreadPoolExecutor.defaultExecutorName());
    }

    public DynamicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler, String executorName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.executorId = new StringGenerator.UUIDGenerator().next();
        this.executorName = executorName;
        ExecutorMonitor.register(this);
    }

    @Override
    protected void terminated() {
        super.terminated();
        ExecutorMonitor.offline(executorId);
    }

    private static String defaultExecutorName() {
        return "threadpool-" + threadPoolNumber.getAndIncrement();
    }

}
