package com.share.co.kcl.dtp.monitor.security.authorization.voter;

import com.alibaba.fastjson2.JSON;
import com.share.co.kcl.dtp.monitor.security.authentication.entity.DtpMonitorUserDetails;
import com.share.co.kcl.dtp.monitor.security.authorization.configattribute.ConsoleAuthConfigAttribute;
import com.share.co.kcl.dtp.monitor.security.authorization.configattribute.MonitorSignConfigAttribute;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.Objects;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
public class SignVoter implements AccessDecisionVoter<MethodInvocation> {

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute instanceof MonitorSignConfigAttribute;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, MethodInvocation object, Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
            throw new BadCredentialsException("身份认证失败");
        }

        for (ConfigAttribute attribute : attributes) {
            if (this.supports(attribute)) {
                if (Objects.isNull(authentication.getPrincipal())) {
                    throw new BadCredentialsException("鉴权失败");
                }
                if (!(authentication.getPrincipal() instanceof DtpMonitorUserDetails)) {
                    throw new BadCredentialsException("鉴权失败");
                }
                DtpMonitorUserDetails dtpMonitorUserDetails = (DtpMonitorUserDetails) authentication.getPrincipal();
                if (StringUtils.isBlank(dtpMonitorUserDetails.getPrincipal())
                        || StringUtils.isBlank(dtpMonitorUserDetails.getUsername())) {
                    throw new BadCredentialsException("鉴权失败");
                }
            }
        }

        return ACCESS_GRANTED;
    }
}
