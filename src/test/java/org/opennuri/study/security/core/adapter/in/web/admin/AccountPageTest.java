package org.opennuri.study.security.core.adapter.in.web.admin;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opennuri.study.security.core.application.port.in.*;
import org.opennuri.study.security.core.domain.Account;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties =
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class AccountPageTest {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    RoleManageUseCase roleManageUseCase;

    @Autowired
    RegisterAccountUseCase registerAccountUseCase;

    @Autowired
    private AccountManageUseCase accountManageUseCase;

    @BeforeEach
    public void setUp() {
        webClient = new WebClient();
        loadData();
    }

    public void loadData() {
        getRoleList().stream().map(role -> RoleManageCommand.builder()
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .build()
        ).forEach(roleManageUseCase::register);

        getAccountList().stream().map(account -> RegisterAccountCommand.builder()
                .name(account.getUsername())
                .password(account.getPassword())
                .email(account.getEmail())
                .roles(account.getRoles())
                .build()
        ).forEach(registerAccountUseCase::register);
    }

    @AfterEach
    public void tearDown() {
        webClient.close();
        deleteDat();
    }

    private void deleteDat() {
        accountManageUseCase.deleteAll();
        roleManageUseCase.deleteAll();
    }

    @Test
    @DisplayName("account 목록은 3개이다.")
    void account_list_success() throws IOException {

        HtmlPage resultPage = webClient.getPage("http://localhost:" + port + "/admin/accounts");
        assertThat(resultPage.getByXPath("//table[@class='table']/tbody/tr")
                .size()).isEqualTo(3);
    }

    @Test
    @DisplayName("account 상세정보는 5개의 form-group을 가지고 있다.")
    void account_detail_success() throws IOException {

        HtmlPage resultPage = webClient.getPage("http://localhost:" + port + "/admin/accounts");

        HtmlAnchor anchor = resultPage.getFirstByXPath("//table[@class='table']/tbody/tr[1]/td[1]/a");
        HtmlPage detailPage = anchor.click();

        assertThat(detailPage).isNotNull();
        assertThat(detailPage.getByXPath("//form/div[@class='form-group']"))
                .size().isEqualTo(5);
    }

    @Test
    @DisplayName("account 상세정보는등록버튼 목록 버튼을 가지고 있다.")
    void account_detail_button_success() throws IOException {

        HtmlPage resultPage = webClient.getPage("http://localhost:" + port + "/admin/accounts");

        HtmlAnchor anchor = resultPage.getFirstByXPath("//table[@class='table']/tbody/tr[1]/td[1]/a");
        HtmlPage detailPage = anchor.click();

        assertThat(detailPage).isNotNull();

        assertThat(detailPage.getAnchorByHref("/admin/accounts").getTextContent()).isEqualTo("목록");
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
