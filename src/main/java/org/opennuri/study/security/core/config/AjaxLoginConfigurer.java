package org.opennuri.study.security.core.config;

import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.security.core.application.security.filter.AjaxLoginProcessingFilter;
import org.opennuri.study.security.core.application.security.handler.AjaxAuthenticationFailureHandler;
import org.opennuri.study.security.core.application.security.handler.AjaxAuthenticationSuccessHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Optional;

@Slf4j
public class AjaxLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, AjaxLoginConfigurer<H>, AjaxLoginProcessingFilter> {

    private AuthenticationManager authenticationManager;
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;

    public AjaxLoginConfigurer() {
        super(new AjaxLoginProcessingFilter(), null);
    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);
    }

    @Override
    public void configure(H http) {

        if (authenticationManager == null) {
            authenticationManager = http.getSharedObject(AuthenticationManager.class);
        }
        getAuthenticationFilter().setAuthenticationManager(authenticationManager);
        getAuthenticationFilter().setAuthenticationFailureHandler(authenticationFailureHandler);
        getAuthenticationFilter().setAuthenticationSuccessHandler(authenticationSuccessHandler);

        Optional.ofNullable(http.getSharedObject(SessionAuthenticationStrategy.class))
                .ifPresent(getAuthenticationFilter()::setSessionAuthenticationStrategy);

        Optional.ofNullable(http.getSharedObject(RememberMeServices.class))
                .ifPresent(getAuthenticationFilter()::setRememberMeServices);

        log.info(getAuthenticationFilter().getClass().getCanonicalName());
        http.setSharedObject(AjaxLoginProcessingFilter.class, getAuthenticationFilter());
        log.info(getAuthenticationFilter().getClass().getCanonicalName());
        http.addFilterBefore(getAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    public AjaxLoginConfigurer<H> successHandler(AjaxAuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        return this;
    }

    public AjaxLoginConfigurer<H> failureHandler(AjaxAuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
        return this;
    }

    public AjaxLoginConfigurer<H> authenticationManager(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
        return this;
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }
}
