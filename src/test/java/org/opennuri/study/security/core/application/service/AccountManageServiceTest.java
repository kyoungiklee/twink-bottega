package org.opennuri.study.security.core.application.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opennuri.study.security.core.application.port.in.*;
import org.opennuri.study.security.core.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ComponentScan(basePackages = "org.opennuri.study.security")
@ActiveProfiles("test")
@DisplayName("계정 관리 서비스 테스트")
class AccountManageServiceTest {
    @Autowired
    private AccountManageUseCase accountManageUseCase;
    @Autowired
    private RegisterAccountUseCase registerUserUseCase;
    @Autowired
    private RoleManageUseCase roleManageUseCase;

    @AfterEach
    void tearDown() {
        accountManageUseCase.deleteAll();
        roleManageUseCase.deleteAll();
    }

    private void registerRole(String roleName, String description) {
        RoleManageCommand roleCommand = RoleManageCommand.builder()
                .roleName(roleName)
                .description(description)
                .build();
        roleManageUseCase.register(roleCommand);
    }

    private Account registerAccount(String mail) {
        return registerUserUseCase.register(RegisterAccountCommand.builder()
                .name("test")
                .password("test")
                .email(mail)
                .build());
    }

    @Test
    @DisplayName("계정 수정시 성공 시 Optional<Account>가 반환된다.")
    void updateAccount() {
        // 최초 등록시 ROLE_USER로 사용작 등록
        registerRole("ROLE_USER", "사용자 권한");
        // 변경할 권한 등록
        registerRole("ROLE_ADMIN", "어드민 권한");
        Account register = registerAccount("mail@hotmail.com");

        Optional<Account> optionalAccount = accountManageUseCase.findById(register.getId());

        Account account1 = optionalAccount.orElseThrow();
        // update commnad는 Set<String> 타입 --> usecase 단에서 Set<String>으로 Set<RoleDto>로 변환하여 저장
        UpdateAccountCommand command = UpdateAccountCommand.builder()
                .id(account1.getId())
                .roles(Set.of("ROLE_ADMIN"))
                .build();
        accountManageUseCase.updateAccount(command);

        accountManageUseCase.findById(register.getId()).ifPresent(account ->
                assertThat(account.getRoles().stream().allMatch(role -> role.getRoleName().equals("ROLE_ADMIN")))
                        .isEqualTo(true)

        );
    }

    @Test
    @DisplayName("계정 수정시 실패 시 UsernameNotFoundException이 발생한다.")
    void updateAccount_fail() {
        registerRole("ROLE_USER", "사용자 권한");
        registerAccount("mail@gmail.com");

        assertThatThrownBy(() -> accountManageUseCase.updateAccount(UpdateAccountCommand.builder()
                .id(100L)
                .roles(Set.of("ROLE_USER"))
                .build()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("사용자 정보가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("계정 삭제 성공 시 Account가 삭제된다.")
    void deleteAccount() {
        registerRole("ROLE_USER", "사용자 권한");
        Account register = registerAccount("mail@hotmail.com");

        assertThat(accountManageUseCase.deleteAccount(register.getId())).isTrue();
        assertThat(accountManageUseCase.findById(register.getId())).isEmpty();

    }

    @Test
    @DisplayName("전체 계정 삭제시 성공 시 전체 Account가 삭제된다.")
    void deleteAll() {
        registerRole("ROLE_USER", "사용자 권한");
        registerAccount("mail@gamil.com");
        registerAccount("mail@hotmail.com");

        List<Account> accounts = accountManageUseCase.findAll().orElseThrow();
        assertThat(accounts.size()).isEqualTo(2);

        assertThat(accountManageUseCase.deleteAll()).isTrue();
        assertThat(accountManageUseCase.findAll()).isEmpty();
    }

    @Test
    @DisplayName("전체 계정 조회시 성공 시 Optional<List<Account>>가 반환된다.")
    void findAll() {
        registerRole("ROLE_USER", "사용자 권한");
        registerAccount("mail@hotmail.com");
        registerAccount("mail@gmail.com");

        List<Account> accounts = accountManageUseCase.findAll().orElseThrow();
        assertThat(accounts.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("계정 아이디로 조회 성공 시 Optional<Account>가 반환된다.")
    void findById() {
        registerRole("ROLE_USER", "사용자 권한");
        Account register = registerAccount("mail@hotmail.com");
        accountManageUseCase.findById(register.getId()).ifPresent(account -> {
            assertThat(account.getUsername()).isEqualTo("test");
            assertThat(account.getEmail()).isEqualTo("mail@hotmail.com");
        });
    }

    @Test
    @DisplayName("계정 이름으로 조회시 성공 시 Optional<Account>가 반환된다.")
    void findByUserName() {
        registerRole("ROLE_USER", "사용자 권한");
        Account register = registerAccount("mail@hotmail.com");
        Account account = accountManageUseCase.findByUserName(register.getUsername()).orElseThrow();
        assertThat(account.getUsername()).isEqualTo("test");
        assertThat(account.getEmail()).isEqualTo("mail@hotmail.com");
    }

    @Test
    @DisplayName("계정 이메일로 조회시 성공 시 Optional<Account>가 반환된다.")
    void findByEmail() {
        registerRole("ROLE_USER", "사용자 권한");
        Account register = registerAccount("mail@gmail.com");
        Account account = accountManageUseCase.findByEmail(register.getEmail()).orElseThrow();
        assertThat(account.getUsername()).isEqualTo("test");
        assertThat(account.getEmail()).isEqualTo("mail@gmail.com");
    }

    @Test
    @DisplayName("계정 이름과 이메일로 조회시 성공 시 Optional<Account>가 반환된다.")
    void findByUserNameAndEmail() {
        registerRole("ROLE_USER", "사용자 권한");
        Account register = registerAccount("mail@gmail.com");
        Account account = accountManageUseCase.findByUserNameAndEmail(register.getUsername(), register.getEmail()).orElseThrow();
        assertThat(account.getUsername()).isEqualTo("test");
        assertThat(account.getEmail()).isEqualTo("mail@gmail.com");
    }
}