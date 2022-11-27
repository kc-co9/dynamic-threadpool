package com.share.co.kcl.dtp.monitor.model.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DtpAdministrator extends DtpBase {

    /**
     * 管理者账号
     */
    private String account;

    /**
     * 管理者密码
     */
    private String password;

    /**
     * 管理者用户名
     */
    private String username;

    /**
     * 管理者邮箱
     */
    private String email;

}
