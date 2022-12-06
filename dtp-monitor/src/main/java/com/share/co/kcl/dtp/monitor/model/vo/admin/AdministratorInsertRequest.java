package com.share.co.kcl.dtp.monitor.model.vo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AdministratorInsertRequest {

    @NotBlank(message = "管理者账号不能为空")
    @ApiModelProperty(value = "管理者账号")
    private String account;

    @NotBlank(message = "管理者密码不能为空")
    @ApiModelProperty(value = "管理者密码")
    private String password;

    @NotBlank(message = "管理者用户名不能为空")
    @ApiModelProperty(value = "管理者用户名")
    private String username;

    @NotBlank(message = "管理者邮箱不能为空")
    @ApiModelProperty(value = "管理者邮箱")
    private String email;
}
