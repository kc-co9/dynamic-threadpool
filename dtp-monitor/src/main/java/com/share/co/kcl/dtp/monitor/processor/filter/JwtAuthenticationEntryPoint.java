package com.share.co.kcl.dtp.monitor.processor.filter;

import com.alibaba.fastjson2.JSON;
import com.share.co.kcl.dtp.common.constants.ResultCode;
import com.share.co.kcl.dtp.monitor.model.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败会调用此方法，例如：通过注解{@code @PreAuthorize("isAuthenticated()")}认证失败
 * <p>
 * 详情可阅读<a href=https://docs.spring.io/spring-security/site/docs/5.5.2-SNAPSHOT/reference/html5/#servlet-authentication-authenticationentrypoint></a>
 * <p>
 * 注意，此处虽然有实现相应逻辑，但是过滤器在advice之后执行，所以在advice就被拦截了，即异常逻辑在advice中处理。
 *
 * @author kcl.co
 * @since 2022/02/19
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        Result<?> response = Result.error(ResultCode.AUTH_FAIL, "身份认证失败");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().print(JSON.toJSONString(response));
    }
}
