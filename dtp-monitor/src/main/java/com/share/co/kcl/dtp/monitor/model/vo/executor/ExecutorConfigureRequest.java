package com.share.co.kcl.dtp.monitor.model.vo.executor;

import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ExecutorConfigureRequest {
    @NotNull(message = "服务ID不能为空")
    @ApiModelProperty(value = "服务ID")
    private Long serverId;

    @NotBlank(message = "服务IP不能为空")
    @ApiModelProperty(value = "服务IP")
    private String serverIp;

    @NotBlank(message = "线程池ID不能为空")
    @ApiModelProperty(value = "线程池ID")
    private String executorId;

    @NotNull(message = "线程池配置不能为空")
    @ApiModelProperty(value = "线程池配置信息")
    private ExecutorConfigBo executorConfig;
}
