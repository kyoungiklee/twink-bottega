package org.opennuri.study.security.core.application.service;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opennuri.study.security.core.application.port.in.RegisterRoleHierarchyCommand;
import org.opennuri.study.security.core.application.port.in.RoleHierarchyManageUseCase;
import org.opennuri.study.security.core.application.port.in.UpdateRoleHierarchyCommand;
import org.opennuri.study.security.core.application.port.out.RoleHierarchyPersistencePort;
import org.opennuri.study.security.core.domain.RoleHierarchy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@ActiveProfiles("test")
@DisplayName("RoleHierarchy 관리 시")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class RoleHierarchyManageServiceTest {

    @BeforeEach
    void setUp() {
        RegisterRoleHierarchyCommand registerAdminRoleHierarchyCommand = RegisterRoleHierarchyCommand.builder()
                .name("ROLE_ADMIN")
                .parentName("ROOT")
                .build();
        RoleHierarchy admimRoleHierarchy
                = roleHierarchyManageUseCase.registerRoleHierarchy(registerAdminRoleHierarchyCommand);

        RegisterRoleHierarchyCommand registerManagerRoleHierarchyCommand = RegisterRoleHierarchyCommand.builder()
                .name("ROLE_MANAGER")
                .parentName("ROLE_ADMIN")
                .build();

        managerRoleHierarchy
                = roleHierarchyManageUseCase.registerRoleHierarchy(registerManagerRoleHierarchyCommand);

        RegisterRoleHierarchyCommand registerUserRoleHierarchyCommand = RegisterRoleHierarchyCommand.builder()
                .name("ROLE_USER")
                .parentName("ROLE_MANAGER")
                .build();
        userRoleHierarchy
                = roleHierarchyManageUseCase.registerRoleHierarchy(registerUserRoleHierarchyCommand);

    }

    @AfterEach
    void tearDown() {
        roleHierarchyPersistencePort.deleteAll();
    }

    @Autowired
    private RoleHierarchyManageUseCase roleHierarchyManageUseCase;

    @Autowired
    private RoleHierarchyPersistencePort roleHierarchyPersistencePort;

    private RoleHierarchy userRoleHierarchy;

    private RoleHierarchy managerRoleHierarchy;

    @Test
    @Order(1)
    @DisplayName("RoleHierarchy 등록을 성공한다.")
    void registerRoleHierarchy_success() {

        RegisterRoleHierarchyCommand command = RegisterRoleHierarchyCommand.builder()
                .name("ROLE_PRODUCT_MANAGER")
                .parentName("ROLE_MANAGER")
                .build();

        RoleHierarchy roleHierarchy = roleHierarchyManageUseCase.registerRoleHierarchy(command);

        assertThat(roleHierarchy).isNotNull();
        assertThat(roleHierarchy.getName()).isEqualTo("ROLE_PRODUCT_MANAGER");
        assertThat(roleHierarchy.getParentName()).isEqualTo("ROLE_MANAGER");

    }

    @Test
    @Order(2)
    @DisplayName("RoleHierarchy 수정을 성공한다.")
    void modifyRoleHierarchy() {

        roleHierarchyManageUseCase.findRoleHierarchyById(userRoleHierarchy.getId()).ifPresentOrElse(
                roleHierarchy -> {
                    RoleHierarchy updatedRoleHierarchy = roleHierarchyManageUseCase.modifyRoleHierarchy(
                            UpdateRoleHierarchyCommand.builder()
                                    .id(roleHierarchy.getId())
                                    .name("ROLE_PRODUCT_MANAGER")
                                    .parentName("ROLE_ADMIN")
                                    .build()
                    );
                    assertThat(updatedRoleHierarchy).isNotNull();
                    assertThat(updatedRoleHierarchy.getName()).isEqualTo("ROLE_PRODUCT_MANAGER");
                    assertThat(updatedRoleHierarchy.getParentName()).isEqualTo("ROLE_ADMIN");

                }
                , () -> fail("RoleHierarchy가 존재하지 않습니다.")
        );

    }

    @Test
    @Order(7)
    @DisplayName("id로 RoleHierarchy 삭제를 성공한다.")
    void removeRoleHierarchy() {

        roleHierarchyManageUseCase.removeRoleHierarchy(userRoleHierarchy.getId());
        roleHierarchyManageUseCase.findRoleHierarchyById(userRoleHierarchy.getId()).ifPresentOrElse(
                roleHierarchy -> fail("RoleHierarchy가 삭제되지 않았습니다.")
                , () -> {}
        );
    }

    @Test
    @Order(8)
    @DisplayName("name으로 RoleHierarchy 삭제를 성공한다.")
    void testRemoveRoleHierarchy() {
        roleHierarchyManageUseCase.removeRoleHierarchy("ROLE_USER");
        roleHierarchyManageUseCase.findRoleHierarchyByName("ROLE_USER").ifPresentOrElse(
                roleHierarchy -> fail("RoleHierarchy가 삭제되지 않았습니다.")
                , () -> {}
        );
    }

    @Test
    @Order(3)
    @DisplayName("RoleHierarchy 이름으로 조회를 성공한다.")
    void findRoleHierarchyById() {

        roleHierarchyManageUseCase.findRoleHierarchyById(managerRoleHierarchy.getId()).ifPresentOrElse(
                roleHierarchy -> {
                    assertThat(roleHierarchy).isNotNull();
                    assertThat(roleHierarchy.getName()).isEqualTo("ROLE_MANAGER");
                    assertThat(roleHierarchy.getParentName()).isEqualTo("ROLE_ADMIN");
                }
                , () -> fail("RoleHierarchy가 존재하지 않습니다.")
        );
    }


    @Test
    @Order(4)
    @DisplayName("RoleHierarchy parent 이름으로 조회를 성공한다.")
    void findRoleHierarchyByParentName() {
        List<RoleHierarchy> roleHierarchyList = roleHierarchyManageUseCase.findRoleHierarchyByParentName("ROLE_ADMIN");
        assertThat(roleHierarchyList).isNotNull();
        assertThat(roleHierarchyList.size()).isEqualTo(1);
        assertThat(roleHierarchyList.get(0).getName()).isEqualTo("ROLE_MANAGER");
        assertThat(roleHierarchyList.get(0).getParentName()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @Order(5)
    @DisplayName("RoleHierarchy 이름으로 조회를 성공한다.")
    void findRoleHierarchyByName() {
        roleHierarchyManageUseCase.findRoleHierarchyByName("ROLE_USER").ifPresentOrElse(
                roleHierarchy -> {
                    assertThat(roleHierarchy).isNotNull();
                    assertThat(roleHierarchy.getName()).isEqualTo("ROLE_USER");
                    assertThat(roleHierarchy.getParentName()).isEqualTo("ROLE_MANAGER");
                }
                , () -> fail("RoleHierarchy가 존재하지 않습니다.")
        );
    }

    @Test
    @Order(6)
    @DisplayName("모든 RoleHierarchy를 조회한다.")
    void findAllRoleHierarchy() {
        List<RoleHierarchy> allRoleHierarchy = roleHierarchyManageUseCase.findAllRoleHierarchy();
        assertThat(allRoleHierarchy).isNotNull();
        assertThat(allRoleHierarchy.size()).isEqualTo(3);
        assertThat(allRoleHierarchy).extracting("name")
                .contains("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_USER");

        assertThat(allRoleHierarchy).extracting("parentName")
                .contains("", "ROLE_ADMIN", "ROLE_MANAGER");

        assertThat(allRoleHierarchy).extracting("name", "parentName")
                .contains(
                        new Tuple("ROLE_ADMIN", "")
                        , new Tuple("ROLE_MANAGER", "ROLE_ADMIN")
                        , new Tuple("ROLE_USER", "ROLE_MANAGER"));
    }
}