package com.share.co.kcl.dtp.monitor.security.authentication.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
public class DtpMonitorUserDetails implements UserDetails {

    private String serverCode;
    private String serverName;

    public DtpMonitorUserDetails() {
    }

    public DtpMonitorUserDetails(String serverCode, String serverName) {
        this.serverCode = serverCode;
        this.serverName = serverName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    public String getPrincipal() {
        return this.serverCode;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.serverName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
