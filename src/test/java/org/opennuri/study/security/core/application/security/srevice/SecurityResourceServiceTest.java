package org.opennuri.study.security.core.application.security.srevice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opennuri.study.security.core.application.port.in.RegisterResourceCommand;
import org.opennuri.study.security.core.application.port.in.ResourceManageUseCase;
import org.opennuri.study.security.core.application.port.in.RoleManageCommand;
import org.opennuri.study.security.core.application.port.in.RoleManageUseCase;
import org.opennuri.study.security.core.application.port.out.ResourcePersistencePort;
import org.opennuri.study.security.core.application.port.out.RolePersistencePort;
import org.opennuri.study.security.core.domain.Resource;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class SecurityResourceServiceTest {
    @Autowired
    private ResourcePersistencePort resourcePersistencePort;

    @Autowired
    private ResourceManageUseCase resourceManageUseCase;

    @Autowired
    private RoleManageUseCase roleManageUseCase;

    @BeforeEach
    void setUp() {
        getRoles().forEach(role -> {
            roleManageUseCase.register(RoleManageCommand.builder()
                    .roleName(role.getRoleName())
                    .description(role.getDescription())
                    .build()
            );
        });

        getResources().forEach(resource -> {
            resourceManageUseCase.createResource(RegisterResourceCommand.builder()
                            .resourceName(resource.getResourceName())
                            .resourceType(resource.getResourceType())
                            .httpMethod(resource.getHttpMethod())
                            .description(resource.getDescription())
                            .orderNum(resource.getOrderNum())
                            .roles(resource.getRoleSet()
                                    .stream().map(Role::getRoleName)
                                    .collect(Collectors.toSet()))
                    .build());
        });
    }

    @AfterEach
    void tearDown() {
        // 데이터 삭제시 순서 주의
        resourceManageUseCase.deleteAll();
        roleManageUseCase.deleteAll();
    }

    @Test
    @DisplayName("리소스이름과 권한 정보로 구성된 3개의 정보를 가지는 Map을 만든다.")
    void getResourceList() {
        SecurityResourceService service = new SecurityResourceService(resourcePersistencePort);
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourceMap = service.getResourceList();
        assertThat(resourceMap.size()).isEqualTo(3);

        Collection<List<ConfigAttribute>> values = resourceMap.values();
        Set<String> authorityNames = values.stream()
                .flatMap(list ->
                        list.stream().map(ConfigAttribute::getAttribute)).collect(Collectors.toSet());

        assertThat(authorityNames).matches(authority -> authority.contains("ROLE_ADMIN"));
        assertThat(authorityNames).matches(authority -> authority.contains("ROLE_MANAGER"));
        assertThat(authorityNames).matches(authority -> authority.contains("ROLE_USER"));
    }

    private List<Resource> getResources() {

        return List.of(
                Resource.from(
                        new Resource.Id(1L)
                        , new Resource.ResourceName("/config")
                        , new Resource.Description("test")
                        , new Resource.ResourceType("url")
                        , new Resource.HttpMethod("GET")
                        , new Resource.OrderNum(1)
                        , new Resource.Roles(new HashSet<>(getRoles().subList(0, 1)))
                )
                , Resource.from(
                        new Resource.Id(1L)
                        , new Resource.ResourceName("/messages")
                        , new Resource.Description("test")
                        , new Resource.ResourceType("url")
                        , new Resource.HttpMethod("GET")
                        , new Resource.OrderNum(2)
                        , new Resource.Roles(new HashSet<>(getRoles().subList(1, 2)))
                )
                , Resource.from(
                        new Resource.Id(1L)
                        , new Resource.ResourceName("/mypage")
                        , new Resource.Description("test")
                        , new Resource.ResourceType("url")
                        , new Resource.HttpMethod("GET")
                        , new Resource.OrderNum(3)
                        , new Resource.Roles(new HashSet<>(getRoles().subList(2, 3)))
                )
        );
    }

    private List<Role> getRoles() {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.from(
                new Role.Id(1L)
                , new Role.RoleName("ROLE_ADMIN")
                , new Role.Description("어드민 권한")
        ));
        roles.add(Role.from(
                new Role.Id(2L)
                , new Role.RoleName("ROLE_MANAGER")
                , new Role.Description("관리자 권한")
        ));
        roles.add(Role.from(
                new Role.Id(3L)
                , new Role.RoleName("ROLE_USER")
                , new Role.Description("사용자 권한")
        ));
        return roles;
    }
}