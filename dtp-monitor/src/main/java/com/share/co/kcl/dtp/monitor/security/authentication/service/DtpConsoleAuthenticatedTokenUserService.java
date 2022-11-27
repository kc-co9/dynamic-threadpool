package com.share.co.kcl.dtp.monitor.security.authentication.service;

import com.share.co.kcl.dtp.monitor.security.authentication.entity.DtpConsoleAuthenticationToken;
import com.share.co.kcl.dtp.monitor.security.authentication.entity.DtpConsoleUserDetails;
import com.share.co.kcl.dtp.monitor.security.authentication.entity.DtpConsoleUserGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DtpConsoleAuthenticatedTokenUserService implements AuthenticationUserDetailsService<DtpConsoleAuthenticationToken> {

    @Override
    public UserDetails loadUserDetails(DtpConsoleAuthenticationToken token) throws UsernameNotFoundException {
        String userId = String.valueOf(token.getPrincipal());
        String username = "admin";
        GrantedAuthority grantedAuthority = new DtpConsoleUserGrantedAuthority("");
        return new DtpConsoleUserDetails(userId, username, Collections.singleton(grantedAuthority));
    }
}
