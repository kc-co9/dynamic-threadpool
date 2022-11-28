package com.share.co.kcl.dtp.common.model.dto;

import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.common.model.bo.ExecutorStatisticsBo;
import lombok.Data;

@Data
public class ExecutorReportDto {

    /**
     * 线程池ID
     */
    private String executorId;

    /**
     * 线程池名称
     */
    private String executorName;

    /**
     * 线程池配置信息
     */
    private ExecutorConfigBo configBody;

    /**
     * 线程池统计信息
     */
    private ExecutorStatisticsBo statisticsBody;

}
