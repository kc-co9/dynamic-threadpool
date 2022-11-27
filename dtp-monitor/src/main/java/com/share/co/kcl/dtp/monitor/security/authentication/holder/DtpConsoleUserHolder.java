package com.share.co.kcl.dtp.monitor.security.authentication.holder;

import com.share.co.kcl.dtp.monitor.security.authentication.entity.DtpConsoleUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Optional;

public class DtpConsoleUserHolder {

    private DtpConsoleUserHolder() {
    }

    public static String getAdministratorId() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(PreAuthenticatedAuthenticationToken.class::isInstance)
                .map(Authentication::getPrincipal)
                .map(DtpConsoleUserDetails.class::cast)
                .map(DtpConsoleUserDetails::getPrincipal)
                .orElse("");
    }


    public static String getAdministratorName() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(PreAuthenticatedAuthenticationToken.class::isInstance)
                .map(Authentication::getPrincipal)
                .map(DtpConsoleUserDetails.class::cast)
                .map(DtpConsoleUserDetails::getUsername)
                .orElse("");
    }
}
