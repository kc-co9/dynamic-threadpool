package com.share.co.kcl.dtp.monitor.security;

import com.share.co.kcl.dtp.monitor.security.authentication.DtpMonitorProcessingFilter;
import com.share.co.kcl.dtp.monitor.security.authentication.DtpConsoleProcessingFilter;
import com.share.co.kcl.dtp.monitor.security.authentication.entity.DtpConsoleAuthenticationToken;
import com.share.co.kcl.dtp.monitor.security.authentication.entity.DtpMonitorAuthenticationToken;
import com.share.co.kcl.dtp.monitor.security.authentication.manager.DtpConsoleAuthenticationProvider;
import com.share.co.kcl.dtp.monitor.security.authentication.manager.DtpMonitorAuthenticationProvider;
import com.share.co.kcl.dtp.monitor.security.authentication.service.DtpConsoleAuthenticatedTokenUserService;
import com.share.co.kcl.dtp.monitor.security.authentication.service.DtpMonitorAuthenticatedTokenUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationUserDetailsService<DtpConsoleAuthenticationToken> dtpConsoleAuthenticationUserDetailsService;
    private final AuthenticationUserDetailsService<DtpMonitorAuthenticationToken> dtpMonitorAuthenticationUserDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    public WebSecurityConfig(
            DtpConsoleAuthenticatedTokenUserService dtpConsoleAuthenticationUserDetailsService,
            DtpMonitorAuthenticatedTokenUserService dtpMonitorAuthenticationUserDetailsService,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler) {
        this.dtpConsoleAuthenticationUserDetailsService = dtpConsoleAuthenticationUserDetailsService;
        this.dtpMonitorAuthenticationUserDetailsService = dtpMonitorAuthenticationUserDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        DtpConsoleAuthenticationProvider dtpConsoleAuthenticationProvider = new DtpConsoleAuthenticationProvider();
        dtpConsoleAuthenticationProvider.setPreAuthenticatedUserDetailsService(this.dtpConsoleAuthenticationUserDetailsService);
        auth.authenticationProvider(dtpConsoleAuthenticationProvider);

        DtpMonitorAuthenticationProvider dtpMonitorAuthenticationProvider = new DtpMonitorAuthenticationProvider();
        dtpMonitorAuthenticationProvider.setPreAuthenticatedUserDetailsService(this.dtpMonitorAuthenticationUserDetailsService);
        auth.authenticationProvider(dtpMonitorAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 跨域
                .cors()
                .and()
                // CSRF
                .csrf().disable()
                // header
                .headers()
                .httpStrictTransportSecurity().disable()
                .frameOptions().disable()
                .and()
                // 配置 anonymous
                .anonymous()
                .principal(0)
                .and()
                .addFilterAt(new DtpConsoleProcessingFilter(authenticationManager()), AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterAt(new DtpMonitorProcessingFilter(authenticationManager()), AbstractPreAuthenticatedProcessingFilter.class)
                // 授权异常
                .exceptionHandling()
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .accessDeniedHandler(this.accessDeniedHandler)
                // 不创建会话
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 默认所有请求通过，在需要权限的方法加上安全注解
                .and()
                .authorizeRequests()
                .anyRequest().permitAll()
        ;
    }

    /**
     * 跨域配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //同源配置，*表示任何请求都视为同源，若需指定ip和端口可以改为如“localhost：8080”，多个以“，”分隔；
        corsConfiguration.addAllowedOrigin("*");
        //header，允许哪些header，本案中使用的是token，此处可将*替换为token；
        corsConfiguration.addAllowedHeader("*");
        //允许的请求方法，POST、GET等
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        //配置允许跨域访问的url
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
