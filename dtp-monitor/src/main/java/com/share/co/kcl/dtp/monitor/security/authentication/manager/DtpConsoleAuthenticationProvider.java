package com.share.co.kcl.dtp.monitor.security.authentication.manager;

import com.share.co.kcl.dtp.monitor.security.authentication.entity.DtpConsoleAuthenticationToken;

public class DtpConsoleAuthenticationProvider extends AbstractDtpAuthenticationProvider<DtpConsoleAuthenticationToken> {

    /**
     * Indicate that this provider only supports PreAuthenticatedAuthenticationToken
     * (sub)classes.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return DtpConsoleAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
