package com.share.co.kcl.dtp.common.model.bo;

import lombok.Data;

/**
 * 统计数据
 */
@Data
public class ExecutorStatisticsBo {

    /**
     * 线程池ID
     */
    private String executorId;

    /**
     * 线程池名称
     */
    private String executorName;

    /**
     * 队列类型
     */
    private String queueClass;

    /**
     * 队列节点数量
     */
    private int queueNodeCount;

    /**
     * 队列剩余容量
     */
    private int queueRemainingCapacity;

    /**
     * 线程池当前线程数量
     */
    private int poolSize;

    /**
     * 线程池曾经出现过的最大线程池数量
     */
    private int largestPoolSize;

    /**
     * 线程池正在执行任务的线程数量
     */
    private int activeCount;

    /**
     * 线程池已被调度执行的任务数量
     */
    private long taskCount;

    /**
     * 线程池已被执行完成的任务数量
     */
    private long completedTaskCount;

}
