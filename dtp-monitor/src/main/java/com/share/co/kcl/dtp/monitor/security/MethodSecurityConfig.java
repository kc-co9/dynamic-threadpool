package com.share.co.kcl.dtp.monitor.security;

import com.share.co.kcl.dtp.monitor.security.authorization.metadatasource.DtpDelegatingMetadataSource;
import com.share.co.kcl.dtp.monitor.security.authorization.metadatasource.impl.MonitorSignMetadataSource;
import com.share.co.kcl.dtp.monitor.security.authorization.metadatasource.impl.ConsoleAuthMetadataSource;
import com.share.co.kcl.dtp.monitor.security.authorization.metadatasource.impl.ConsolePermissionMetadataSource;
import com.share.co.kcl.dtp.monitor.security.authorization.voter.AuthVoter;
import com.share.co.kcl.dtp.monitor.security.authorization.voter.PermissionVoter;
import com.share.co.kcl.dtp.monitor.security.authorization.voter.SignVoter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.method.DelegatingMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    /**
     * 重写AccessDecisionManager，让自定义的AuthorizeVoter和PermissionVoter具有更高的优先级
     */
    @Override
    protected AccessDecisionManager accessDecisionManager() {
        AccessDecisionManager accessDecisionManager = super.accessDecisionManager();
        if (Objects.nonNull(accessDecisionManager)
                && accessDecisionManager instanceof AffirmativeBased) {
            AffirmativeBased affirmativeBased = (AffirmativeBased) accessDecisionManager;
            List<AccessDecisionVoter<?>> voters = new ArrayList<>();
            voters.add(new AuthVoter());
            voters.add(new PermissionVoter());
            voters.add(new SignVoter());
            voters.addAll(affirmativeBased.getDecisionVoters());
            return new UnanimousBased(voters);
        }

        return accessDecisionManager;
    }

    /**
     * 重写MethodSecurityMetadataSource，让自定义的AuthorizeMetadataSource和PermissionMetadataSource具有更高的优先级
     */
    @Bean
    @Override
    public MethodSecurityMetadataSource methodSecurityMetadataSource() {
        DelegatingMethodSecurityMetadataSource delegating = (DelegatingMethodSecurityMetadataSource) super.methodSecurityMetadataSource();
        List<MethodSecurityMetadataSource> metadataSourceList = new ArrayList<>();
        metadataSourceList.add(new ConsoleAuthMetadataSource());
        metadataSourceList.add(new ConsolePermissionMetadataSource());
        metadataSourceList.add(new MonitorSignMetadataSource());
        metadataSourceList.addAll(delegating.getMethodSecurityMetadataSources());
        return new DtpDelegatingMetadataSource(metadataSourceList);
    }


}
