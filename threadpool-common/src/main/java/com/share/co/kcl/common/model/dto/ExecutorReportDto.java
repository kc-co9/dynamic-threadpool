package com.share.co.kcl.common.model.dto;

import com.share.co.kcl.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.common.model.bo.ExecutorStatisticsBo;
import lombok.Data;

@Data
public class ExecutorReportDto {

    private String executorId;

    private String executorName;

    private ExecutorConfigBo configBody;

    private ExecutorStatisticsBo statisticsBody;

}
