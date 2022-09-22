package com.share.co.kcl.dtp.common.model.bo;

import lombok.Data;

/**
 * 统计数据
 */
@Data
public class ExecutorStatisticsBo {

    private String executorId;

    private String executorName;

    private int poolSize;

    private int largestPoolSize;

    private int activeCount;

    private long taskCount;

    private long completedTaskCount;
}
