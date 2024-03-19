package org.opennuri.study.security.core.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opennuri.study.security.core.application.port.in.*;
import org.opennuri.study.security.core.domain.Resource;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.AfterTestClass;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("리소스 관리 서비스 테스트")
class ResourceManageServiceTest {

    @Autowired
    private ResourceManageUseCase resourceManageUseCase;

    @Autowired
    private RoleManageUseCase roleManageUseCase;

    @Autowired
    private AccountManageUseCase accountManageUseCase;

    @BeforeEach
    void setUp() {
        resourceManageUseCase.deleteAll();
        accountManageUseCase.deleteAll();
        roleManageUseCase.deleteAll();
    }

    @AfterTestClass
    void tearDown() {
        resourceManageUseCase.deleteAll();
        accountManageUseCase.deleteAll();
        roleManageUseCase.deleteAll();
    }

    private Resource registerResource(Set<String> roles) {


        return resourceManageUseCase.createResource(RegisterResourceCommand.builder()
                .resourceName("test")
                .resourceType("test")
                .httpMethod("GET")
                .description("test")
                .orderNum(1)
                .roles(roles)
                .build());
    }

    private Set<Role> registerRole(String... roleNames) {
        HashSet<Role> roles = new HashSet<>();
        Arrays.stream(roleNames).map(roleName -> RoleManageCommand.builder()
                .roleName(roleName)
                .description("test")
                .build())
                .forEach(roleManageCommand -> roles.add(roleManageUseCase.register(roleManageCommand)));
        return roles;
    }


    @Test
    @DisplayName("리소스 생성에 성공하면 리소스를 반환한다.")
    void createResource() {
        Set<Role> roleSet = registerRole("ROLE_USER", "ROLE_MANAGER", "ROLE_ADMIN");

        Resource resource = registerResource(roleSet.stream().map(Role::getRoleName).collect(Collectors.toSet()));

        assertThat(resource).isNotNull();
        assertThat(resource.getId()).isNotNull();
        assertThat(resource.getResourceName()).isEqualTo("test");
        assertThat(resource.getResourceType()).isEqualTo("test");
        assertThat(resource.getHttpMethod()).isEqualTo("GET");
        assertThat(resource.getDescription()).isEqualTo("test");
        assertThat(resource.getOrderNum()).isEqualTo(1);
        assertThat(resource.getRoleSet()).isNotEmpty();

    }

    @Test
    @DisplayName("리소스 수정에 성공하면 리소스를 반환한다.")
    void updateResource() {
        Set<Role> roleSet = registerRole("ROLE_USER");

        Resource resource = registerResource(roleSet.stream().map(Role::getRoleName).collect(Collectors.toSet()));

        Set<Role> newRoleSet = registerRole("ROLE_ADMIN", "ROLE_MANAGER");

        resource.getRoleSet().removeIf(role -> role.getRoleName().equals("ROLE_USER"));
        resource.getRoleSet().addAll(newRoleSet);


        UpdateResourceCommand updateResourceCommand = UpdateResourceCommand.builder()
                .id(resource.getId())
                .resourceName("로그인요청")
                .resourceType("API")
                .httpMethod("POST")
                .description("로그인 요청 API")
                .orderNum(2)
                .roles(resource.getRoleSet().stream()
                        .map(Role::getRoleName).collect(Collectors.toSet()))
                .build();

        Resource updatedResource = resourceManageUseCase.updateResource(updateResourceCommand);

        assertThat(updatedResource).isNotNull();
        assertThat(updatedResource.getId()).isEqualTo(resource.getId());
        assertThat(updatedResource.getResourceName()).isEqualTo(updateResourceCommand.getResourceName());
        assertThat(updatedResource.getResourceType()).isEqualTo(updateResourceCommand.getResourceType());
        assertThat(updatedResource.getHttpMethod()).isEqualTo(updateResourceCommand.getHttpMethod());
        assertThat(updatedResource.getDescription()).isEqualTo(updateResourceCommand.getDescription());
        assertThat(updatedResource.getOrderNum()).isEqualTo(updateResourceCommand.getOrderNum());
        assertThat(updatedResource.getRoleSet()).isNotEmpty();
        assertThat(updatedResource.getRoleSet().stream().map(Role::getRoleName)
                .collect(Collectors.toSet())).contains("ROLE_ADMIN", "ROLE_MANAGER");

    }

    @Test
    @DisplayName("리소스 삭제에 성공하면 true 반환한다.")
    void deleteResource() {
        Set<Role> roleSet = registerRole("ROLE_USER");
        Resource resource = registerResource(roleSet.stream().map(Role::getRoleName).collect(Collectors.toSet()));

        assertThat(resourceManageUseCase.deleteResource(resource.getId())).isTrue();
        assertThat(resourceManageUseCase.findById(resource.getId())).isEmpty();
    }

    @Test
    @DisplayName("리소스 아이디로 조회에 성공하면 리소스의 옵셔널을 반환한다.")
    void findById() {
        Set<Role> roleSet = registerRole("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER");
        Resource resource = registerResource(roleSet.stream().map(Role::getRoleName).collect(Collectors.toSet()));

        assertThat(resourceManageUseCase.findById(resource.getId())).isNotEmpty();
        resourceManageUseCase.findById(resource.getId()).ifPresent(findResource -> {
            assertThat(findResource.getId()).isEqualTo(resource.getId());
            assertThat(findResource.getResourceName()).isEqualTo(resource.getResourceName());
            assertThat(findResource.getResourceType()).isEqualTo(resource.getResourceType());
            assertThat(findResource.getHttpMethod()).isEqualTo(resource.getHttpMethod());
            assertThat(findResource.getDescription()).isEqualTo(resource.getDescription());
            assertThat(findResource.getOrderNum()).isEqualTo(resource.getOrderNum());
            assertThat(findResource.getRoleSet().stream().map(Role::getRoleName)
                    .collect(Collectors.toSet())).contains("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER");
        });
    }

    @Test
    @DisplayName("리소스 이름으로 조회에 성공하면 리소스의 옵셔널을 반환한다.")
    void findByResourceName() {

        Set<Role> roleSet = registerRole("ROLE_USER", "ROLE_ADMIN");

        Resource resource = registerResource(roleSet.stream().map(Role::getRoleName).collect(Collectors.toSet()));

        assertThat(resourceManageUseCase.findByResourceName(resource.getResourceName())).isNotEmpty();
        resourceManageUseCase.findByResourceName(resource.getResourceName()).ifPresent(findResource -> {
            assertThat(findResource.getId()).isEqualTo(resource.getId());
            assertThat(findResource.getResourceName()).isEqualTo(resource.getResourceName());
            assertThat(findResource.getResourceType()).isEqualTo(resource.getResourceType());
            assertThat(findResource.getHttpMethod()).isEqualTo(resource.getHttpMethod());
            assertThat(findResource.getDescription()).isEqualTo(resource.getDescription());
            assertThat(findResource.getOrderNum()).isEqualTo(resource.getOrderNum());
            assertThat(findResource.getRoleSet().stream().map(Role::getRoleName)
                    .collect(Collectors.toSet())).contains("ROLE_USER", "ROLE_ADMIN");
        });
    }

    @Test
    @DisplayName("리소스 전체 삭제에 성공하면 void 반환한다.")
    void deleteAll() {
        Set<Role> roleSet = registerRole("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER");

        for (int i = 0; i < 10; i++) {
            registerResource(roleSet.stream().map(Role::getRoleName).collect(Collectors.toSet()));
        }
        assertThat(resourceManageUseCase.deleteAll()).isTrue();
        assertThat(resourceManageUseCase.findAll()).isEmpty();

    }

    @Test
    @DisplayName("리소스 전체 조회에 성공하면 리소스 리스트의 옵셔널을 반환한다.")
    void findAll() {
        Set<Role> roleSet = registerRole("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER");

        for (int i = 0; i < 2; i++) {
            registerResource(roleSet.stream().map(Role::getRoleName).collect(Collectors.toSet()));
        }
        assertThat(resourceManageUseCase.findAll()).isNotEmpty();
        resourceManageUseCase.findAll().ifPresent(resources -> assertThat(resources).hasSize(2));
    }
}