package com.share.co.kcl.dtp.monitor.model.vo.admin;

import com.share.co.kcl.dtp.monitor.model.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdministratorsGetRequest extends Page {
    /**
     * 管理者账号
     */
    @ApiModelProperty(value = "管理者账号")
    private String account;

    /**
     * 管理者用户名
     */
    @ApiModelProperty(value = "管理者用户名")
    private String username;

    /**
     * 管理者邮箱
     */
    @ApiModelProperty(value = "管理者邮箱")
    private String email;
}
