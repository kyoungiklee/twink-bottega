package org.opennuri.study.security.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.security.core.application.security.common.FormAuthenticationDetailsSource;
import org.opennuri.study.security.core.application.security.factory.UrlResourcesFactoryBean;
import org.opennuri.study.security.core.application.security.filter.PermitAllFilter;
import org.opennuri.study.security.core.application.security.handler.CustomAccessDeniedHandler;
import org.opennuri.study.security.core.application.security.handler.CustomAuthenticationFailureHandler;
import org.opennuri.study.security.core.application.security.handler.CustomAuthenticationSuccessHandler;
import org.opennuri.study.security.core.application.security.metadatasource.UrlFilterInvocationSecurityMetadatasource;
import org.opennuri.study.security.core.application.security.provider.CustomAuthenticationProvider;
import org.opennuri.study.security.core.application.security.srevice.SecurityResourceService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Order(1)
@Profile({"!test", "security"})
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final FormAuthenticationDetailsSource formAuthenticationDetailsSource;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final SecurityResourceService securityResourceService;

    private final String[] permitAllResources = {"/", "/register", "/login*", "/denied"};

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(customAuthenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                /*.authorizeRequests()
                .antMatchers("/mypage").hasRole("USER")
                .antMatchers("/maessages").hasRole("MAANGER")
                .antMatchers("/config").hasRole("ADMIN")
                .antMatchers("/", "/register", "/login*").permitAll()
                .anyRequest().authenticated()
        .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_process")
                .authenticationDetailsSource(formAuthenticationDetailsSource)
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler)
                .permitAll()
        .and()
                .exceptionHandling()
                .accessDeniedPage("/denied")
                .accessDeniedHandler(customAccessDeniedHandler)*/

                /*.authorizeHttpRequests(
                        request -> {
                            request.antMatchers("/", "/register", "/login*").permitAll();
                            request.antMatchers("/mypage").hasRole("USER");
                            request.antMatchers("/messages").hasRole("MANAGER");
                            request.antMatchers("/config").hasRole("ADMIN");
                            request.anyRequest().authenticated();
                        })*/
                .formLogin(formLoginConfigurer -> {
                    formLoginConfigurer.loginPage("/login");
                    formLoginConfigurer.defaultSuccessUrl("/");
                    formLoginConfigurer.loginProcessingUrl("/login_process");
                    formLoginConfigurer.authenticationDetailsSource(formAuthenticationDetailsSource);
                    formLoginConfigurer.successHandler(customAuthenticationSuccessHandler);
                    formLoginConfigurer.failureHandler(customAuthenticationFailureHandler);
                })
                .exceptionHandling(exceptionHandlingConfigurer -> {
                    exceptionHandlingConfigurer.accessDeniedPage("/denied");
                    exceptionHandlingConfigurer.accessDeniedHandler(customAccessDeniedHandler);
                })
                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class)
        ;
        log.info("SecurityConfig: configure()");

    }

    // 스프링부트 3.0 이상부터는 FileterSecurityInterceptor 가 deprecated 되었으므로 사용하지 않는다.
    // Deprecated Use AuthorizationFilter instead
    // todo: springboot 3.0 이상에서 적용하기 위해 FilterSecurityInterceptor 사용하지 않는 방법 찾아보기(2021.10.14)
    //
    /*@Bean
    public FilterSecurityInterceptor customFilterSecurityInterceptor() throws Exception {
        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
        filterSecurityInterceptor.setSecurityMetadataSource(urlFilterInvocationSecurityMetadatasource());
        filterSecurityInterceptor.setAccessDecisionManager(affirmativeBased());
        filterSecurityInterceptor.setAuthenticationManager(authenticationManagerBean());
        return filterSecurityInterceptor;
    }*/

    @Bean
    public PermitAllFilter customFilterSecurityInterceptor() throws Exception {
        PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllResources);
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadatasource());
        permitAllFilter.setAccessDecisionManager(affirmativeBased());
        permitAllFilter.setAuthenticationManager(authenticationManagerBean());
        return permitAllFilter;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();

    }

    private AccessDecisionManager affirmativeBased() {
        return new AffirmativeBased(getAccessDecisionVoters());
    }

    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {
        return List.of(new RoleVoter());
    }

    @Bean
    public FilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadatasource() throws Exception {
        return new UrlFilterInvocationSecurityMetadatasource(urlResourcesFactoryBean().getObject());
    }

    @Bean
    public UrlResourcesFactoryBean urlResourcesFactoryBean() {
       return new UrlResourcesFactoryBean(securityResourceService);
    }
}