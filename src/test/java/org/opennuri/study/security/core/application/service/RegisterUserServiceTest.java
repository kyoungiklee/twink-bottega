package org.opennuri.study.security.core.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opennuri.study.security.core.application.port.in.*;
import org.opennuri.study.security.core.domain.Account;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.AfterTestClass;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class RegisterUserServiceTest {
    @Autowired
    private RegisterAccountUseCase registerUserUseCase;

    @Autowired
    private AccountManageUseCase accountManageUseCase;

    @Autowired
    private RoleManageUseCase roleManageUseCase;

    @BeforeEach
    void setUp() {
        accountManageUseCase.deleteAll();
        roleManageUseCase.deleteAll();
    }

    @AfterTestClass
    void tearDown() {
        accountManageUseCase.deleteAll();
        roleManageUseCase.deleteAll();
    }

    @Nested
    @DisplayName("사용자 등록 시")
    class RegisterUserTest {
        @Test
        @DisplayName("사용자 등록 성공 시 사용자 정보가 반환된다.")
        void register() {
            RoleManageCommand roleCommand = RoleManageCommand.builder()
                    .roleName("ROLE_USER")
                    .description("일반 사용자")
                    .build();
            roleManageUseCase.register(roleCommand);

            RegisterAccountCommand command = RegisterAccountCommand.builder()
                    .name("test")
                    .password("test")
                    .email("mail@gmail.com")
                    .build();
            Account register = registerUserUseCase.register(command);
            assertThat(register.getId()).isNotNull();
        }

        @Test
        @DisplayName("인자값이 null인 경우 ConstraintViolationException이 발생한다.")
        void register_parameters_null() {
            assertThatThrownBy(() -> registerUserUseCase.register(
                    RegisterAccountCommand.builder()
                            .name(null)
                            .password(null)
                            .email(null)
                            .build()))
                    .isInstanceOf(ConstraintViolationException.class)
                    .hasMessageContaining("name: 비어 있을 수 없습니다"
                            , "password: 비어 있을 수 없습니다"
                            , "email: 비어 있을 수 없습니다");
        }

        @ParameterizedTest(name = "{index} => email={0}")
        @ValueSource(strings = {"", " ", "mail", "mail@com", "mail.com", "mail@"
                , "mail@.com", "mail@com.", "mail@com. "
                , "mail@com.1", "mail@com.1 ", "mail@com.1 1"
                , "mail$mail@mail.com", "mail^mail@mail.com", "mail*@mail.com"
        })
        @DisplayName("이메일 형식이 아닌 경우 ConstraintViolationException이 발생한다.")
        void register_invalid_email(String email) {
            assertThatThrownBy(() ->
                    RegisterAccountCommand.builder()
                            .name("test")
                            .password("test")
                            .email(email)
                            .build())
                    .isInstanceOf(ConstraintViolationException.class)
                    .hasMessage("email: 올바른 형식의 이메일 주소여야 합니다");
        }

        @Test
        @DisplayName("사용자 이메일 중복 시 DuplicateKeyException이 발생한다.")
        void register_duplicate_email() {
            RoleManageCommand roleCommand = RoleManageCommand.builder()
                    .roleName("ROLE_USER")
                    .description("일반 사용자")
                    .build();
            roleManageUseCase.register(roleCommand);

            RegisterAccountCommand command = RegisterAccountCommand.builder()
                    .name("test")
                    .password("test")
                    .email("mail@gmail.com")
                    .build();
            registerUserUseCase.register(command);

            assertThatThrownBy(() -> registerUserUseCase.register(command))
                    .isInstanceOf(DuplicateKeyException.class);
        }

        @Test
        @DisplayName("사용자 최초 등록 시 권한은 일반 사용자로 설정된다.")
        void register_default_role() {
            RoleManageCommand roleCommand = RoleManageCommand.builder()
                    .roleName("ROLE_USER")
                    .description("일반 사용자")
                    .build();
            roleManageUseCase.register(roleCommand);

            RegisterAccountCommand command = RegisterAccountCommand.builder()
                    .name("test")
                    .password("test")
                    .email("mail@gmail.com")
                    .build();
            Account register = registerUserUseCase.register(command);
            Set<Role> roles = register.getRoles();
            List<String> collect = roles.stream().map(Role::getRoleName).collect(Collectors.toList());

            assertThat(collect).asList().hasSize(1);
            assertThat(collect).asList().contains("ROLE_USER");

        }

        @ParameterizedTest(name = "{index} => password={0}")
        @ValueSource(strings = {"", " ", "test", "test1", "test1!", "test1!1"})
        @DisplayName("패스워드는 8자 이상 대문자, 소문자, 숫자, 특수문자를 포함해야 하며 공백이 없어야한다.")
        void register_invalid_password(String password) {

            String passwordRegexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
            assertThat(password.matches(passwordRegexp)).isFalse();
        }
    }
}
