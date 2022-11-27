package com.share.co.kcl.dtp.monitor.security.authentication;

import com.share.co.kcl.dtp.monitor.model.domain.SecurityAuthDo;
import com.share.co.kcl.dtp.monitor.constants.RequestParamsConstants;
import com.share.co.kcl.dtp.monitor.security.authentication.entity.DtpConsoleAuthenticationToken;
import com.share.co.kcl.dtp.monitor.utils.SecurityUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class DtpConsoleProcessingFilter extends AbstractDtpProcessingFilter {

    public DtpConsoleProcessingFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(RequestParamsConstants.TOKEN))
                .map(SecurityUtils::parseToken)
                .map(SecurityAuthDo::getUserId)
                .orElse(null);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return request.getHeader(RequestParamsConstants.TOKEN);
    }

    @Override
    protected PreAuthenticatedAuthenticationToken createAuthenticationToken(Object principal, Object credentials) {
        return new DtpConsoleAuthenticationToken(principal, credentials);
    }
}
