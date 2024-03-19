package org.opennuri.study.security.core.adapter.in.web.front.membership;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opennuri.study.security.core.application.port.in.RegisterAccountCommand;
import org.opennuri.study.security.core.application.port.in.RegisterAccountUseCase;
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

import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = RegisterAccountController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
        , excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                SecurityConfig.class,
                AjaxSecurityConfig.class,
        })
}
)
class RegisterAccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private RegisterAccountUseCase registerAccountUseCase;

    @Test
    public void register() throws Exception {
        RegisterAccountCommand command = RegisterAccountCommand.builder()
                .name("test")
                .password("test")
                .email("mail+1@gmail.com")
                .roles(Set.of(Role.from(
                        new Role.Id(null),
                        new Role.RoleName("ROLE_USER"),
                        new Role.Description("일반 사용자")
                )))
                .build();

        Account account = Account.from(
                new Account.Id(1L)
                , new Account.Username("test")
                , new Account.Password("test")
                , new Account.Email("mail+1@gmail.com")
                , new Account.Roles(Set.of(Role.from(
                        new Role.Id(1L)
                        , new Role.RoleName("ROLE_USER")
                        , new Role.Description("일반 사용자")
                )))
        );

        given(registerAccountUseCase.register(command)).willReturn(account);

        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        // RegisterUserRequest로 부터 MultiValueMap으로 변환
        valueMap.add("name", "test");
        valueMap.add("password", "test");
        valueMap.add("email", "mail+1@gmail.com");

        mockMvc.perform(post("/register")
                .params(valueMap))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/**"))
                ;
    }

}