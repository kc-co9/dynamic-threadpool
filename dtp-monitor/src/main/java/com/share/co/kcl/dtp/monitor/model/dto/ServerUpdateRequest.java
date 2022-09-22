package com.share.co.kcl.dtp.monitor.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ServerUpdateRequest {

    @NotNull(message = "服务ID不能为空")
    @ApiModelProperty(value = "服务ID")
    private Long serverId;

    @NotBlank(message = "服务代码不能为空")
    @ApiModelProperty(value = "服务代码")
    private String serverCode;

    @NotBlank(message = "服务名称不能为空")
    @ApiModelProperty(name = "服务名称")
    private String serverName;

}
