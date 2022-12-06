package com.share.co.kcl.dtp.monitor.model.vo.server;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ServerDeleteRequest {

    @NotNull(message = "服务ID不能为空")
    @ApiModelProperty(value = "服务ID")
    private Long serverId;

}
