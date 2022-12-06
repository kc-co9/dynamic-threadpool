package com.share.co.kcl.dtp.monitor.model.vo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdministratorDeleteRequest {
    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID")
    private Long id;

}
