package com.share.co.kcl.dtp.monitor.security.authentication;

import com.share.co.kcl.dtp.monitor.constants.RequestParamsConstants;
import com.share.co.kcl.dtp.monitor.security.authentication.entity.DtpMonitorAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class DtpMonitorProcessingFilter extends AbstractDtpProcessingFilter {

    public DtpMonitorProcessingFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(RequestParamsConstants.SIGN_SECRET)).orElse("");
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(RequestParamsConstants.SIGN_SECRET)).orElse("");
    }

    @Override
    protected PreAuthenticatedAuthenticationToken createAuthenticationToken(Object principal, Object credentials) {
        return new DtpMonitorAuthenticationToken(principal, credentials);
    }
}
