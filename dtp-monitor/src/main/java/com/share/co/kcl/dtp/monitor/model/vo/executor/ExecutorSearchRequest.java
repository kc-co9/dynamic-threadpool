package com.share.co.kcl.dtp.monitor.model.vo.executor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ExecutorSearchRequest {

    @NotNull(message = "服务ID不能为空")
    @ApiModelProperty(value = "服务ID")
    private Long serverId;

    @NotBlank(message = "服务IP不能为空")
    @ApiModelProperty(value = "服务IP")
    private String serverIp;

    @ApiModelProperty(value = "线程池ID")
    private String executorId;

    @ApiModelProperty(value = "线程池名字")
    private String executorName;

}
