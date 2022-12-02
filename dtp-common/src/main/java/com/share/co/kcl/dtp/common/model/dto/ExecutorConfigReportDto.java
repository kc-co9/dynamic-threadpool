package com.share.co.kcl.dtp.common.model.dto;

import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorConfigReportDto {

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
    private ExecutorConfigBo executorConfig;

}
