package com.share.co.kcl.dtp.monitor.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ExecutorSyncLookupRequest {
    @NotBlank(message = "服务代码不能为空")
    @ApiModelProperty(value = "服务代码")
    private String serverCode;

    @NotBlank(message = "服务IP不能为空")
    @ApiModelProperty(value = "服务IP")
    private String serverIp;
}
