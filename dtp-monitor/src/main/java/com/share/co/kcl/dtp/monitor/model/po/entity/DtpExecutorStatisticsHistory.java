package com.share.co.kcl.dtp.monitor.model.po.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DtpExecutorStatisticsHistory extends DtpBase {

    /**
     * 服务ID
     */
    private Long serverId;

    /**
     * 服务ID
     */
    private String serverIp;

    /**
     * 服务名称
     */
    private String serverName;

    /**
     * 线程池ID
     */
    private String executorId;

    /**
     * 线程池名称
     */
    private String executorName;

    /**
     * 线程池队列类型
     */
    private String executorQueueClass;

    /**
     * 线程池队列节点数量
     */
    private Integer executorQueueNodeCount;

    /**
     * 线程池队列剩余容量
     */
    private Integer executorQueueRemainCapacity;

    /**
     * 线程池当前线程数量
     */
    private Integer executorPoolSize;

    /**
     * 线程池曾经出现过的最大线程池数量
     */
    private Integer executorLargestPoolSize;

    /**
     * 线程池正在执行任务的线程数量
     */
    private Integer executorActiveCount;

    /**
     * 线程池已被调度执行的任务数量
     */
    private Long executorTaskCount;

    /**
     * 线程池已被执行完成的任务数量
     */
    private Long executorCompletedTaskCount;
}
