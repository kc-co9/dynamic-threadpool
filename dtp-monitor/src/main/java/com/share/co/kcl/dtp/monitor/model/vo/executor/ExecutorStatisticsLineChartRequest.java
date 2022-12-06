package com.share.co.kcl.dtp.monitor.model.vo.executor;

import com.share.co.kcl.dtp.monitor.model.enums.LineChartDuration;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ExecutorStatisticsLineChartRequest {

    @NotNull(message = "服务ID不能为空")
    @ApiModelProperty(value = "服务ID")
    private Long serverId;

    @NotBlank(message = "服务IP不能为空")
    @ApiModelProperty(value = "服务IP")
    private String serverIp;

    @NotBlank(message = "线程池ID不能为空")
    @ApiModelProperty(value = "线程池ID")
    private String executorId;

    @NotNull(message = "折线图区间不能为空")
    @ApiModelProperty(value = "折线图区间")
    private LineChartDuration duration;

}
