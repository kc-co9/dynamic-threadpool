package com.share.co.kcl.dtp.monitor.model.po.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DtpServer extends DtpBase {

    /**
     * 服务代码
     */
    private String serverCode;

    /**
     * 服务名称
     */
    private String serverName;

    /**
     * 服务密钥
     */
    private String serverSecret;
}
