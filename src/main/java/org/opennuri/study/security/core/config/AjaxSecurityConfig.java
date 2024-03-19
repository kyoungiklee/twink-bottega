package org.opennuri.study.security.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.security.core.application.security.common.AjaxLoginAuthenticationEntryPoint;
import org.opennuri.study.security.core.application.security.handler.AjaxAccessDeniedHandler;
import org.opennuri.study.security.core.application.security.handler.AjaxAuthenticationFailureHandler;
import org.opennuri.study.security.core.application.security.handler.AjaxAuthenticationSuccessHandler;
import org.opennuri.study.security.core.application.security.provider.AjaxAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@Order(0)
@RequiredArgsConstructor
@Slf4j
@Profile({"!test", "security"})
public class AjaxSecurityConfig extends WebSecurityConfigurerAdapter {
    private final AjaxAuthenticationProvider ajaxAuthenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(ajaxAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/api/**")
                .authorizeHttpRequests(authorizeHttpRequest -> {
                    authorizeHttpRequest.antMatchers("/api/login").permitAll();
                    authorizeHttpRequest.antMatchers("/api/messages").hasRole("MANAGER");
                    authorizeHttpRequest.anyRequest().authenticated();
                })
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                        .authenticationEntryPoint(ajaxLoginAuthenticationEntryPoint())
                        .accessDeniedHandler(ajaxAccessDeniedHandler()));

        customAuthenticationFilter(http);
        log.info("AjaxSecurigyConfig: configure()");
    }

    /**
     * ajax 방식의 로그인 처리를 위한 configure 처리
     * @param http - security 객체
     * @throws Exception - apply() 시 예외 발생
     */
    private void customAuthenticationFilter(HttpSecurity http) throws Exception {
        http.apply(new AjaxLoginConfigurer<>())
                .authenticationManager(authenticationManagerBean())
                .failureHandler(ajaxAuthenticationFailureHandler())
                .successHandler(ajaxAuthenticationSuccessHandler())
                .loginProcessingUrl("/api/login");
    }

    @Bean
    public AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler() {
        return new AjaxAuthenticationFailureHandler();
    }

    @Bean
    public AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler() {
        return new AjaxAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationEntryPoint ajaxLoginAuthenticationEntryPoint() {
        return new AjaxLoginAuthenticationEntryPoint();
    }

    @Bean
    public AccessDeniedHandler ajaxAccessDeniedHandler() {
        return new AjaxAccessDeniedHandler();
    }

    @Bean
    public AuthenticationManager ajaxAuthenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
