package com.share.co.kcl.dtp.monitor.model.dto;

import com.share.co.kcl.dtp.monitor.model.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServerSearchRequest extends Page {

    @ApiModelProperty(value = "服务ID")
    private Long serverId;

    @ApiModelProperty(value = "服务代码")
    private String serverCode;

    @ApiModelProperty(value = "服务名称")
    private String serverName;
}
