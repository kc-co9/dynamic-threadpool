package com.share.co.kcl.dtp.monitor.security.authorization.voter;

import com.share.co.kcl.dtp.monitor.security.authorization.configattribute.ConsoleAuthConfigAttribute;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Collection;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
public class AuthVoter implements AccessDecisionVoter<MethodInvocation> {

    private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute instanceof ConsoleAuthConfigAttribute;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, MethodInvocation object, Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
            throw new BadCredentialsException("用户认证失败");
        }

        for (ConfigAttribute attribute : attributes) {
            if (this.supports(attribute)) {
                boolean isAnonymous = authenticationTrustResolver.isAnonymous(authentication);
                if (isAnonymous) {
                    throw new BadCredentialsException("用户认证失败");
                }
            }
        }

        return ACCESS_GRANTED;
    }
}
