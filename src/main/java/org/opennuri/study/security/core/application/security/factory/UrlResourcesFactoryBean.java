package org.opennuri.study.security.core.application.security.factory;

import lombok.Getter;
import org.opennuri.study.security.core.application.security.srevice.SecurityResourceService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

public class UrlResourcesFactoryBean implements FactoryBean<LinkedHashMap<RequestMatcher, List<ConfigAttribute>>> {
    private final SecurityResourceService securityResourceService;

    @Getter
    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourcesMap;

    public UrlResourcesFactoryBean(SecurityResourceService securityResourceService) {
        this.securityResourceService = securityResourceService;
    }

    @Override
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getObject() throws Exception {
        if(resourcesMap == null) {
            init();
        }
        return resourcesMap;
    }

    private void init() {
        resourcesMap = securityResourceService. getResourceList();
    }

    @Override
    public Class<?> getObjectType() {
        return LinkedHashMap.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
