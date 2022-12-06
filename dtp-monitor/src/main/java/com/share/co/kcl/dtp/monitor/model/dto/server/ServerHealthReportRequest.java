package com.share.co.kcl.dtp.monitor.model.dto.server;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ServerHealthReportRequest {

    @NotBlank(message = "服务实例代码不能为空")
    @ApiModelProperty(value = "服务实例代码")
    private String serverCode;

    @NotBlank(message = "服务实例IP不能为空")
    @ApiModelProperty(value = "服务实例IP")
    private String serverIp;
}
