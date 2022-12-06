package com.share.co.kcl.dtp.monitor.security.authentication.service;

import com.share.co.kcl.dtp.monitor.model.po.entity.DtpServer;
import com.share.co.kcl.dtp.monitor.security.authentication.entity.DtpMonitorAuthenticationToken;
import com.share.co.kcl.dtp.monitor.security.authentication.entity.DtpMonitorUserDetails;
import com.share.co.kcl.dtp.monitor.service.DtpServerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DtpMonitorAuthenticatedTokenUserService implements AuthenticationUserDetailsService<DtpMonitorAuthenticationToken> {

    @Autowired
    private DtpServerService dtpServerService;

    @Override
    public UserDetails loadUserDetails(DtpMonitorAuthenticationToken token) throws UsernameNotFoundException {
        String secret = String.valueOf(token.getPrincipal());
        if (StringUtils.isBlank(secret)) {
            throw new BadCredentialsException("鉴权失败");
        }
        Optional<DtpServer> dtpServer = dtpServerService.getFirst(dtpServerService.getQueryWrapper()
                .eq(DtpServer::getServerSecret, secret));
        String serverCode = dtpServer.map(DtpServer::getServerCode).orElse("");
        String serverName = dtpServer.map(DtpServer::getServerName).orElse("");
        return new DtpMonitorUserDetails(serverCode, serverName);
    }
}
