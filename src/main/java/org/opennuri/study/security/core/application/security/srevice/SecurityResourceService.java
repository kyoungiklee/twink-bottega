package org.opennuri.study.security.core.application.security.srevice;

import org.opennuri.study.security.core.application.port.out.ResourcePersistencePort;
import org.opennuri.study.security.core.application.security.metadatasource.UrlFilterInvocationSecurityMetadatasource;
import org.opennuri.study.security.core.domain.Resource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SecurityResourceService {

    private final ResourcePersistencePort resourcePersistencePort;

    public SecurityResourceService(ResourcePersistencePort resourcePersistencePort) {
        this.resourcePersistencePort = resourcePersistencePort;
    }

    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {
        Optional<List<Resource>> resourcesOptional = resourcePersistencePort.findAllResources();

        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();

        if (resourcesOptional.isPresent()) {
            List<Resource> resources = resourcesOptional.get();
            resources.forEach(
                    resource -> {
                        List<ConfigAttribute> securityConfigList = resource.getRoleSet().stream().map(
                                        role -> new SecurityConfig(role.getRoleName()))
                                .collect(Collectors.toList());
                        result.put(new AntPathRequestMatcher(resource.getResourceName()), securityConfigList);
                    });
        }
        return result;
    }

    public void reloadMetaData() {
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> reloadedMap = getResourceList();
       // urlFilterInvocationSecurityMetadatasource 를 getBean()으로 가져와서 setRequestMap을 호출하여 새로운 맵을 넣어준다.

        UrlFilterInvocationSecurityMetadatasource.setRequestMap(reloadedMap);


    }
}