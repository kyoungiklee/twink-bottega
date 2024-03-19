package org.opennuri.study.security.core.adapter.in.web.admin;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opennuri.study.security.core.application.port.in.RoleManageCommand;
import org.opennuri.study.security.core.application.port.in.RoleManageUseCase;
import org.opennuri.study.security.core.config.AjaxSecurityConfig;
import org.opennuri.study.security.core.config.SecurityConfig;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = RoleController.class
        , excludeAutoConfiguration = SecurityAutoConfiguration.class
        , excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
            AjaxSecurityConfig.class
            , SecurityConfig.class
        })
)
@DisplayName("role 관리 시")
class RoleControllerTest {

    @MockBean
    private RoleManageUseCase roleManageUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("role 리스트를 조회한다.")
    void getRoles_success() throws Exception {

        given(roleManageUseCase.findAll()).willReturn(Optional.of(getRoleList()));

        mockMvc.perform(get("/admin/roles"))
                .andDo(print())
                .andExpect(status().isOk())
                ;
    }

    @Test
    @DisplayName("role등록 화면을 요청한다.")
    void registerForm_request_success() throws Exception {

        mockMvc.perform(get("/admin/roles/register"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    @DisplayName("role을 등록한다.")
    void register_role_success() throws Exception {

        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        valueMap.add("roleName", "ADMIN_ROLE");
        valueMap.add("description", "어드민 권한");

        RoleManageCommand command = RoleManageCommand.builder()
                .roleName("ADMIN_ROLE")
                .description("어드민 권한")
                .build();

        Role role = Role.from(
                new Role.Id(1L)
                , new Role.RoleName(Objects.requireNonNull(valueMap.get("roleName")).toString())
                , new Role.Description(Objects.requireNonNull(valueMap.get("description")).toString())
        );
        given(roleManageUseCase.register(command)).willReturn(role);

        mockMvc.perform(post("/admin/roles/register")
                        .params(valueMap))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
        ;
    }

    @Test
    @DisplayName("role 등록시 파라미터를 검증한다.")
    void register_role_param_fail() throws Exception {
        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        valueMap.add("roleName", null);
        valueMap.add("description", null);
        mockMvc.perform(post("/admin/roles/register").params(valueMap))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/role/detail"))
        ;
    }

    @Test
    @DisplayName("role 상세를 조회한다.")
    void getRole_success() throws Exception {

        given(roleManageUseCase.findById(1L)).willReturn(getRoleList().stream().findFirst());

        mockMvc.perform(get("/admin/roles/1/edit"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }



    @Test
    @DisplayName("role을 삭제한다.")
    void removeRoles_success() throws Exception {

        given(roleManageUseCase.delete(1L)).willReturn(true);
        mockMvc.perform(get("/admin/roles/1/delete"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
        ;
    }

    private List<Role> getRoleList() {
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