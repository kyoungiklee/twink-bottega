package org.opennuri.study.security.core.adapter.in.web.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opennuri.study.security.core.application.port.in.RegisterResourceCommand;
import org.opennuri.study.security.core.application.port.in.ResourceManageUseCase;
import org.opennuri.study.security.core.application.port.in.RoleManageUseCase;
import org.opennuri.study.security.core.config.AjaxSecurityConfig;
import org.opennuri.study.security.core.config.SecurityConfig;
import org.opennuri.study.security.core.domain.Resource;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ResourceController.class
        , excludeAutoConfiguration = SecurityAutoConfiguration.class
        , excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        AjaxSecurityConfig.class
                        , SecurityConfig.class
                })}
)
@DisplayName("resource 관리 시")
class ResourceControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private ResourceManageUseCase resourceManageUseCase;
    @MockBean
    private RoleManageUseCase roleManageUseCase;

    @Test
    @DisplayName("리소스 목록을 조회한다.")
    void getResources_list_success() throws Exception {
        given(resourceManageUseCase.findAll()).willReturn(Optional.of(getResourceList()));
        mockMvc.perform(get("/admin/resources"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("리소스가 목록이 없는 경우 화면은 정상으로 출력된다.")
    void getResource_list_empty() throws Exception {
        given(resourceManageUseCase.findAll()).willReturn(Optional.empty());
        mockMvc.perform(get("/admin/resources"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("리소스 상세정보를 보여준다.")
    void getResource_resource_detail_success() throws Exception {
        given(resourceManageUseCase.findById(1L)).willReturn(Optional.of(getResourceList().get(0)));
        given(roleManageUseCase.findAll()).willReturn(Optional.of(getRoles()));

        mockMvc.perform(get("/admin/resources/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("리소스가 존재하지 않는 경우 빈값으로 보여진다.")
    void getResource_resource_empty() throws Exception {
        given(resourceManageUseCase.findById(1L)).willReturn(Optional.empty());
        given(roleManageUseCase.findAll()).willReturn(Optional.empty());

        mockMvc.perform(get("/admin/resources/1"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    @DisplayName(("리소스 등록화면을 출력한다."))
    void getRegisterForm() throws Exception {
        given(roleManageUseCase.findAll()).willReturn(Optional.of(getRoles()));

        mockMvc.perform(get("/admin/resources/register"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    @DisplayName("리소스가 등록된다.")
    void register_resource_success() throws Exception {

        RegisterResourceCommand command = RegisterResourceCommand.builder()
                .resourceName("/admin")
                .resourceType("url")
                .httpMethod("GET")
                .orderNum(1)
                .roles(Set.of("ROLE_ADMIN"))
                .description("test")
                .build();
        given(resourceManageUseCase.createResource(command)).willReturn(getResourceList().get(0));

        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        valueMap.add("resourceName", "/admin");
        valueMap.add("resourceType", "url");
        valueMap.add("httpMethod", "GET");
        valueMap.add("orderNum", "1");
        valueMap.add("roles", "ROLE_ADMIN");
        valueMap.add("description", "test");

        mockMvc.perform(post("/admin/resources/register")
                        .params(valueMap))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
        ;
    }

    @Test
    @DisplayName("리소스 등록시 입력값을 검증한다.")
    void register_resource_param_validate() throws Exception {

        StringBuilder descriptionString = toStringBuilder(100);
        StringBuilder httpMethodString = toStringBuilder(10);

        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        valueMap.add("resourceName", "");
        valueMap.add("resourceType", "");
        valueMap.add("httpMethod", httpMethodString.toString());
        valueMap.add("orderNum", "-1");
        valueMap.add("roles", "");
        valueMap.add("description", descriptionString.toString());

        mockMvc.perform(post("/admin/resources/register")
                        .params(valueMap))
                .andDo(print())
                .andExpect(status().isOk())
        ;

    }

    private static StringBuilder toStringBuilder(int length) {
        StringBuilder descriptionString = new StringBuilder();
        descriptionString.append("가".repeat(length));
        descriptionString.append("나");
        return descriptionString;
    }

    @Test
    @DisplayName("리소스를 삭제한다.")
    void delete_resource_success() throws Exception {

        Resource resource = getResourceList().get(0);

        given(resourceManageUseCase.findById(1L)).willReturn(Optional.ofNullable(resource));
        given(resourceManageUseCase.deleteResource(1L)).willReturn(true);

        mockMvc.perform(get("/admin/resources/delete/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    private List<Resource> getResourceList() {

        return List.of(
                Resource.from(
                        new Resource.Id(1L)
                        , new Resource.ResourceName("/admin")
                        , new Resource.Description("test")
                        , new Resource.ResourceType("url")
                        , new Resource.HttpMethod("GET")
                        , new Resource.OrderNum(1)
                        , new Resource.Roles(new HashSet<>(getRoles().subList(0, 1)))
                )
                , Resource.from(
                        new Resource.Id(1L)
                        , new Resource.ResourceName("/main")
                        , new Resource.Description("test")
                        , new Resource.ResourceType("url")
                        , new Resource.HttpMethod("GET")
                        , new Resource.OrderNum(2)
                        , new Resource.Roles(new HashSet<>(getRoles().subList(1, 2)))
                )
                , Resource.from(
                        new Resource.Id(1L)
                        , new Resource.ResourceName("/home")
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