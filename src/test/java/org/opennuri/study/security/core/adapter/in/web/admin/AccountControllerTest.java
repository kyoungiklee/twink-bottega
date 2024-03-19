package org.opennuri.study.security.core.adapter.in.web.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opennuri.study.security.core.application.port.in.AccountManageUseCase;
import org.opennuri.study.security.core.application.port.in.RoleManageUseCase;
import org.opennuri.study.security.core.application.port.in.UpdateAccountCommand;
import org.opennuri.study.security.core.config.AjaxSecurityConfig;
import org.opennuri.study.security.core.config.SecurityConfig;
import org.opennuri.study.security.core.domain.Account;
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
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = AccountController.class
        , excludeAutoConfiguration = SecurityAutoConfiguration.class
        , excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                AjaxSecurityConfig.class
                , SecurityConfig.class
        })
})
@DisplayName("사용자 권한 관리 시")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountManageUseCase accountManageUseCase;

    @MockBean
    private RoleManageUseCase roleManageUseCase;

    @Test
    @DisplayName("사용자 전체 목록을 가져온다.")
    void getAccounts() throws Exception {
        given(accountManageUseCase.findAll()).willReturn(Optional.of(getAccountList()));

        mockMvc.perform(get("/admin/accounts"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    @DisplayName("사용자 상세정보를 가져온다")
    void getAccount_detail() throws Exception {
        given(accountManageUseCase.findById(1L)).willReturn(getAccountList().stream().findFirst());
        given(roleManageUseCase.findAll()).willReturn(Optional.of(getRoleList()));

        mockMvc.perform(get("/admin/accounts/1"))
                .andDo(print())
                .andExpect(status().isOk())
        ;

    }

    @Test
    @DisplayName("사용자권한을 수정한다.")
    void update_success() throws Exception {

        UpdateAccountCommand updateAccountCommand = UpdateAccountCommand.builder()
                .id(1L)
                .roles(Set.of("ROLE_ADMIN"))
                .build();

        Account account = Account.from(
                new Account.Id(1L)
                , new Account.Username("test")
                , new Account.Password("test1234")
                , new Account.Email("mail+1@gmail.com")
                , new Account.Roles(new HashSet<>(getRoleList()))
        );
        ;
        given(accountManageUseCase.updateAccount(updateAccountCommand)).willReturn(account);

        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        valueMap.add("id", "1");
        valueMap.add("username", "test");
        valueMap.add("email", "mail+1@gmail.com");
        valueMap.add("password", "test1234");
        valueMap.add("roles", List.of("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_USER").toString());

        mockMvc.perform(post("/admin/accounts")
                        .params(valueMap))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
        ;

    }

    @Test
    @DisplayName("사용자를 삭제한다.")
    void delete_success() throws Exception {

        given(accountManageUseCase.deleteAccount(1L)).willReturn(true);

        mockMvc.perform(get("/admin/accounts/delete/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/admin/accounts"));
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

    private List<Account> getAccountList() {

        List<Account> accountList = new ArrayList<>();
        accountList.add(Account.from(
                new Account.Id(1L)
                , new Account.Username("admin")
                , new Account.Password("admin")
                , new Account.Email("mail+1@gmail.com")
                , new Account.Roles(Set.of(Role.from(
                        new Role.Id(1L)
                        , new Role.RoleName("ROLE_ADMIN")
                        , new Role.Description("어드민 권한")
                )))
        ));
        accountList.add(Account.from(
                new Account.Id(2L)
                , new Account.Username("manager")
                , new Account.Password("manager")
                , new Account.Email("mail+2@gmail.com")
                , new Account.Roles(Set.of(Role.from(
                        new Role.Id(21L)
                        , new Role.RoleName("ROLE_MANAGER")
                        , new Role.Description("관리자 권한")
                )))
        ));
        accountList.add(Account.from(
                new Account.Id(3L)
                , new Account.Username("user")
                , new Account.Password("user")
                , new Account.Email("mail+3@gmail.com")
                , new Account.Roles(Set.of(Role.from(
                        new Role.Id(3L)
                        , new Role.RoleName("ROLE_USER")
                        , new Role.Description("사용자 권한")
                )))
        ));

        return accountList;
    }
}