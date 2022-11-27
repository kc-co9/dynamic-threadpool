package com.share.co.kcl.dtp.monitor.security.authentication.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class DtpConsoleUserDetails implements UserDetails {

    private String userId;
    private String username;
    private Collection<? extends GrantedAuthority> grantedAuthorities;

    public DtpConsoleUserDetails() {
    }

    public DtpConsoleUserDetails(String userId, String username, Collection<? extends GrantedAuthority> grantedAuthorities) {
        this.userId = userId;
        this.username = username;
        this.grantedAuthorities = grantedAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    public String getPrincipal() {
        return this.userId;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.username;
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
