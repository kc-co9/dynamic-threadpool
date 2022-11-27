package com.share.co.kcl.dtp.monitor.model.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.share.co.kcl.dtp.monitor.model.po.DtpAdministrator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdministratorsGetResponse {

    private IPage<Administrators> dtpAdministrators;

    public AdministratorsGetResponse() {
    }

    public AdministratorsGetResponse(IPage<DtpAdministrator> dtpAdministrators) {
        this.dtpAdministrators = dtpAdministrators.convert(dtpAdministrator -> {
            Administrators administrators = new Administrators();
            administrators.setId(dtpAdministrator.getId());
            administrators.setAccount(dtpAdministrator.getAccount());
            administrators.setUsername(dtpAdministrator.getUsername());
            administrators.setEmail(dtpAdministrator.getEmail());
            administrators.setCreateTime(dtpAdministrator.getCreateTime());
            administrators.setUpdateTime(dtpAdministrator.getUpdateTime());
            return administrators;
        });
    }

    @Data
    public static class Administrators {

        @ApiModelProperty(value = "用户id")
        private Long id;

        @ApiModelProperty(value = "管理者账号")
        private String account;

        @ApiModelProperty(value = "管理者用户名")
        private String username;

        @ApiModelProperty(value = "管理者邮箱")
        private String email;

        @ApiModelProperty(value = "创建时间")
        private LocalDateTime createTime;

        @ApiModelProperty(value = "更新时间")
        private LocalDateTime updateTime;
    }
}
