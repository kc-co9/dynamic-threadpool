package com.share.co.kcl.dtp.common.model.bo;

import com.share.co.kcl.dtp.common.enums.RejectedStrategy;
import lombok.Data;

/**
 * 配置数据
 */
@Data
public class ExecutorConfigBo {

    private String executorId;

    private String executorName;

    private int corePoolSize;

    private int maximumPoolSize;

    private long keepAliveTime;

    private RejectedStrategy rejectedStrategy;

}
