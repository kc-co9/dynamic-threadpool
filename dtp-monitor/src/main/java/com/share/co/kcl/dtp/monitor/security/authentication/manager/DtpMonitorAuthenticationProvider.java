package com.share.co.kcl.dtp.monitor.security.authentication.manager;

import com.share.co.kcl.dtp.monitor.security.authentication.entity.DtpMonitorAuthenticationToken;

public class DtpMonitorAuthenticationProvider extends AbstractDtpAuthenticationProvider<DtpMonitorAuthenticationToken> {

    /**
     * Indicate that this provider only supports PreAuthenticatedAuthenticationToken
     * (sub)classes.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return DtpMonitorAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
