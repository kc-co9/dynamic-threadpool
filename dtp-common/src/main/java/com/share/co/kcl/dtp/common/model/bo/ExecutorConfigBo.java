package com.share.co.kcl.dtp.common.model.bo;

import com.share.co.kcl.dtp.common.enums.RejectedStrategy;
import lombok.Data;

/**
 * 配置数据
 */
@Data
public class ExecutorConfigBo {

    /**
     * 线程池ID
     */
    private String executorId;

    /**
     * 线程池名称
     */
    private String executorName;

    /**
     * 线程池核心线程数
     */
    private int corePoolSize;

    /**
     * 线程池最大线程数
     */
    private int maximumPoolSize;

    /**
     * 线程池最大存活时间（秒）
     */
    private long keepAliveTime;

    /**
     * 线程池拒绝策略
     */
    private RejectedStrategy rejectedStrategy;

}
