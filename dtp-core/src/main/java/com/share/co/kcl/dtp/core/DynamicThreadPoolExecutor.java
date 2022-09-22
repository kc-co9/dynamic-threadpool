package com.share.co.kcl.dtp.core;

import com.share.co.kcl.dtp.common.enums.RejectedStrategy;
import com.share.co.kcl.dtp.core.monitor.ExecutorMonitor;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicThreadPoolExecutor extends ThreadPoolExecutor {

    private static final AtomicInteger threadPoolId = new AtomicInteger(1);
    private static final AtomicInteger threadPoolNumber = new AtomicInteger(1);

    /**
     * thread pool id
     */
    @Getter
    @SuppressWarnings("FieldMayBeFinal")
    private String executorId;

    @Getter
    @Setter
    private String executorName;

    public DynamicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), defaultRejectedHandler());
    }

    public DynamicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                     ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, defaultRejectedHandler());
    }

    public DynamicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                     RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), handler);
    }

    public DynamicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                     ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.executorId = defaultExecutorId();
        this.executorName = defaultExecutorName();
        ExecutorMonitor.register(this);
    }

    @Override
    protected void terminated() {
        super.terminated();
        ExecutorMonitor.offline(executorId);
    }

    private static String defaultExecutorId() {
        return String.valueOf(threadPoolId.getAndIncrement());
    }

    private static String defaultExecutorName() {
        return "thread-pool-" + threadPoolNumber.getAndIncrement();
    }

    private static RejectedExecutionHandler defaultRejectedHandler() {
        return RejectedStrategy.defaultRejectedHandler();
    }

}
