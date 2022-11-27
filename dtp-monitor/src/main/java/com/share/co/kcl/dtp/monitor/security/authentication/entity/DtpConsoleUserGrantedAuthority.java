package com.share.co.kcl.dtp.monitor.security.authentication.entity;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
public class DtpConsoleUserGrantedAuthority implements GrantedAuthority {

    private final String permission;

    public DtpConsoleUserGrantedAuthority(String permission) {
        this.permission = permission;
    }

    @Override
    public String getAuthority() {
        return permission;
    }
}
