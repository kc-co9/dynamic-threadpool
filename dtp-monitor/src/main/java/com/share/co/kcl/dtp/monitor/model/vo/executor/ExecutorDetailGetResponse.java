package com.share.co.kcl.dtp.monitor.model.vo.executor;

import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.common.model.bo.ExecutorStatisticsBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorDetailGetResponse {

    @ApiModelProperty(value = "线程池配置信息-设置")
    private ExecutorConfigBo executorConfigSetting;

    @ApiModelProperty(value = "线程池配置信息-实时")
    private ExecutorConfigBo executorConfigMonitor;

    @ApiModelProperty(value = "线程池统计信息-实时")
    private ExecutorStatisticsBo executorStatisticsMonitor;
}
