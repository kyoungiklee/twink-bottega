package org.opennuri.study.security.core.config;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.security.core.application.port.out.ResourcePersistencePort;
import org.opennuri.study.security.core.application.security.factory.UrlResourcesFactoryBean;
import org.opennuri.study.security.core.application.security.metadatasource.UrlFilterInvocationSecurityMetadatasource;
import org.opennuri.study.security.core.application.security.srevice.SecurityResourceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

@Configuration

public class AppConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityResourceService securityResourceService(ResourcePersistencePort resourcePersistencePort) {
        return new SecurityResourceService(resourcePersistencePort);
    }
}
