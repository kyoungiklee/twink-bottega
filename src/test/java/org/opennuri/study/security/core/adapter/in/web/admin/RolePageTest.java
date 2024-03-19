package org.opennuri.study.security.core.adapter.in.web.admin;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.opennuri.study.security.core.application.port.in.RoleManageCommand;
import org.opennuri.study.security.core.application.port.in.RoleManageUseCase;
import org.opennuri.study.security.core.config.YourTestConfig;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
classes = {YourTestConfig.class})
@TestPropertySource(properties =
        {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"}
)
public class RolePageTest {

    private final ModelMapper modelMapper = new ModelMapper();
    private WebClient webClient;

    @Autowired
    private RoleManageUseCase roleManageUseCase;
    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        webClient = new WebClient();
        getRoleList().forEach(role -> roleManageUseCase.register(
                modelMapper.map(role, RoleManageCommand.class)
        ));
    }

    @AfterEach
    public void tearDown() {
        webClient.close();
        roleManageUseCase.deleteAll();
    }

    @Test
    @DisplayName("서버 포트가 0보다 크다.")
    public void testServerPort() {
        System.out.println("port = " + port);
        assertThat(port).isGreaterThan(0);
    }

    @Test
    @DisplayName("role 목록 조회시 테이블 행은 3개이다..")
    public void getRolePage_success() throws Exception {

        HtmlPage page = webClient.getPage("http://localhost:" + port + "/admin/roles");
        assertThat(page).isNotNull();
        assertThat(page.getByXPath("//table[@class='table table-hover']/tbody/tr")
                .size()).isEqualTo(3);
    }

    @Test
    @DisplayName("role 등록화면은 2개의 input을 가지고 있어야 한다.")
    void getRegisterForm_success() throws Exception {
        HtmlPage page = webClient.getPage("http://localhost:" + port + "/admin/roles");

        // 등록하기 버튼을 클릭한다.
        HtmlAnchor registerButton = page.getFirstByXPath("//div[@class='btn-wrp']/a[@class='btn btn-dark btn-lg']");
        HtmlPage resultPage = registerButton.click();

        assertThat(resultPage).isNotNull();
        List<HtmlInput> inputList = resultPage.getByXPath("//input[@class='form-control']");
        assertThat(inputList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("role 등록 후 권한 목록은 4개 이다.")
    void registerRole_success() throws Exception {

        HtmlPage page = webClient.getPage("http://localhost:" + port + "/admin/roles");

        // 등록하기 버튼을 클릭한다.
        HtmlAnchor registerButton = page.getFirstByXPath("//div[@class='btn-wrp']/a[@class='btn btn-dark btn-lg']");
        HtmlPage newRegisterFormPage = registerButton.click();

        //등록 화면에 값을 입력하고 등록 버튼을 클릭한다.
        ((HtmlInput) newRegisterFormPage.getFirstByXPath("//input[@id='roleName']")).setValue("ROLE_TEST");
        ((HtmlInput) newRegisterFormPage.getFirstByXPath("//input[@id='description']")).setValue("테스트 권한");

        // 등록 후 목록 페이지로 이동한다.
        HtmlPage resultPage = ((HtmlButton)newRegisterFormPage.getFirstByXPath("//button[@type='submit']")).click();

        // 목록 페이지에서 테이블 행이 4개인지 확인한다.
        List<HtmlTableRow> tableRows = resultPage.getByXPath("//table[@class='table table-hover']/tbody/tr");
        assertThat(tableRows.size()).isEqualTo(4);

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
