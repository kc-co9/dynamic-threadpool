package com.share.co.kcl.dtp.monitor.model.dto;

import com.share.co.kcl.dtp.common.model.dto.ExecutorStatisticsReportDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ExecutorStatisticsReportRequest {

    @NotBlank(message = "服务代码不能为空")
    @ApiModelProperty(value = "服务代码")
    private String serverCode;

    @NotBlank(message = "服务IP不能为空")
    @ApiModelProperty(value = "服务IP")
    private String serverIp;

    @NotNull(message = "线程池统计信息列表不能为空")
    @ApiModelProperty(value = "线程池统计信息列表")
    private List<ExecutorStatisticsReportDto> executorStatisticsList;
}
