package org.opennuri.study.security.core.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.opennuri.study.security.core.application.port.in.AccountManageUseCase;
import org.opennuri.study.security.core.application.port.in.RoleManageCommand;
import org.opennuri.study.security.core.application.port.in.RoleManageUseCase;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.AfterTestClass;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class RoleManageServiceTest {
    @Autowired
    private RoleManageUseCase roleManageUseCase;
    @Autowired
    private AccountManageUseCase accountManageUseCase;

    @BeforeEach
    public void setup() {
        accountManageUseCase.deleteAll();
        roleManageUseCase.deleteAll();
    }

    @AfterTestClass
    public void tearDown() {
        accountManageUseCase.deleteAll();
        roleManageUseCase.deleteAll();
    }

    @Nested
    @DisplayName("권한 목록 조회 시")
    class FindAllTest {
        @Test
        @DisplayName("성공 케이스인 경우 전체 권한 목록이 반환된다.")
        void findAll_success_return_role_list() {
            RoleManageCommand command = RoleManageCommand.builder()
                    .id(null)
                    .roleName("ROLE_CUSTOMER_MANAGER")
                    .description("이벤트관리자")
                    .build();
            roleManageUseCase.register(command);
            RoleManageCommand command1 = RoleManageCommand.builder()
                    .id(null)
                    .roleName("ROLE_PRODUCT_MANAGER")
                    .description("상품관리자")
                    .build();
            roleManageUseCase.register(command1);

            roleManageUseCase.findAll().ifPresent(roleList -> {
                assertThat(roleList.size()).isEqualTo(2);
                assertThat(roleList.get(0).getRoleName()).isEqualTo("ROLE_CUSTOMER_MANAGER");
                assertThat(roleList.get(1).getRoleName()).isEqualTo("ROLE_PRODUCT_MANAGER");
            });
        }

        @Test
        @DisplayName("성공 케이스의 경우 전체 목록의 갯수를 반환한다.")
        void count_success_return_role_count() {
            RoleManageCommand command = RoleManageCommand.builder()
                    .id(null)
                    .roleName("ROLE_CUSTOMER_MANAGER")
                    .description("이벤트관리자")
                    .build();
            roleManageUseCase.register(command);
            RoleManageCommand command1 = RoleManageCommand.builder()
                    .id(null)
                    .roleName("ROLE_PRODUCT_MANAGER")
                    .description("상품관리자")
                    .build();
            roleManageUseCase.register(command1);

            assertThat(roleManageUseCase.count()).isEqualTo(2);
        }

        @Test
        @DisplayName("성공 케이스인 경우 롤아이디로 조회된 롤 목록을 반환한다.")
        void findAllById_success_return_role() {
            RoleManageCommand command = RoleManageCommand.builder()
                    .id(null)
                    .roleName("ROLE_CUSTOMER_MANAGER")
                    .description("이벤트관리자")
                    .build();
            Role register = roleManageUseCase.register(command);
            RoleManageCommand command1 = RoleManageCommand.builder()
                    .id(null)
                    .roleName("ROLE_PRODUCT_MANAGER")
                    .description("상품관리자")
                    .build();
            Role register1 = roleManageUseCase.register(command1);

            roleManageUseCase.findAllById(List.of(register.getId(), register1.getId())).ifPresent(roleList -> {
                assertThat(roleList.size()).isEqualTo(2);
                assertThat(roleList.stream().allMatch(role -> role.getRoleName().equals("ROLE_CUSTOMER_MANAGER") || role.getRoleName().equals("ROLE_PRODUCT_MANAGER")))
                        .isEqualTo(true);
            });
        }
    }

    @Nested
    @DisplayName("권한이름으로 삭제 시")
    class DeleteByNameTest {
        @Test
        @DisplayName("성공 케이스인 경우 권한이 삭제되고 true를 반환한다.")
        void deleteByName_success_not_found_role() {
            RoleManageCommand command = RoleManageCommand.builder()
                    .id(null)
                    .roleName("ROLE_CUSTOMER_MANAGER")
                    .description("이벤트관리자")
                    .build();

            Role register = roleManageUseCase.register(command);

            assertThat(roleManageUseCase.deleteByName(register.getRoleName())).isTrue();

            Optional<Role> optionalRole = roleManageUseCase.findByName(register.getRoleName());
            assertThat(optionalRole.isEmpty()).isEqualTo(true);
        }

        @Test
        @DisplayName("삭제할 롤 이름이 없는 케이스인 경우 false를 반환한다.")
        void deleteByName_fail_when_role_doesnt_exist() {
            assertThat(roleManageUseCase.deleteByName("ROLE_CUSTOMER_MANAGER")).isEqualTo(false);
        }
    }

    @Nested
    @DisplayName("전체 권한 삭제 시")
    class DeleteAllTest {
        @Test
        @DisplayName("성공 케이스인 경우 전체 권한이 삭제된다.")
        void deleteAll_success_not_found_all_role() {
            RoleManageCommand command = RoleManageCommand.builder()
                    .id(null)
                    .roleName("ROLE_CUSTOMER_MANAGER")
                    .description("이벤트관리자")
                    .build();
            roleManageUseCase.register(command);
            RoleManageCommand command1 = RoleManageCommand.builder()
                    .id(null)
                    .roleName("ROLE_PRODUCT_MANAGER")
                    .description("상품관리자")
                    .build();
            roleManageUseCase.register(command1);

            assertThat(roleManageUseCase.deleteAll()).isTrue();

            assertThat(roleManageUseCase.findAll().isEmpty()).isEqualTo(true);
        }
    }

    @Nested
    @DisplayName("권한 등록 시")
    class RegisterTest {
        @Test
        @DisplayName("성공 게이스인 경우 Role이 반환된다.")
        void register_success_return_role() {
            RoleManageCommand command = RoleManageCommand.builder()
                    .id(null)
                    .roleName("ROLE_PRODUCT_MANAGER")
                    .description("상품관리자")
                    .build();

            Role register = roleManageUseCase.register(command);

            assertThat(register).isNotNull();
            assertThat(register.getId()).isNotNegative();
            assertThat(register.getDescription()).isEqualTo(command.getDescription());
            assertThat(register.getRoleName()).isEqualTo(command.getRoleName());
        }

        @Test
        @DisplayName("인자값이 비정상적인 경우 validation 관련 예외가 발생한다.")
        void register_fail_when_role_name_not_input() {

            // roleName이 등록되지 않는 경우 validation check에서 오류를 발생한다.
            assertThatThrownBy(() ->
                RoleManageCommand.builder()
                        .roleName(null)
                        .description("상품관리자")
                        .build()
            ).isInstanceOf(ConstraintViolationException.class)
                    .hasMessage("roleName: 공백일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("권한 찾기 시")
    class FindByNameTest {
        @Test
        @DisplayName("성공 케이스인 경우 Role이 반환된다.")
        void findByName_success_return_role() {

            RoleManageCommand command = RoleManageCommand.builder()
                    .id(null)
                    .roleName("ROLE_EVENT_MANAGER")
                    .description("이벤트관리자")
                    .build();

            roleManageUseCase.register(command);

            Optional<Role> optionalRole = roleManageUseCase.findByName("ROLE_EVENT_MANAGER");
            assertThat(optionalRole.isPresent()).isEqualTo(true);
        }

        @Test
        @DisplayName("실패 케이스인 경우 Optional empty를 반환한다.")
        void findByName_fail_find_name_doesnt_exist() {
            Optional<Role> optional = roleManageUseCase.findByName("ROLE_CUSTOMER_MANAGER");
            assertThat(optional.isEmpty()).isEqualTo(true);
        }

        @Test
        @DisplayName("롤 정보가 존재하는 케이스인 경우 true를 반환한다.")
        void existsByName_success_return_true() {
            RoleManageCommand command = RoleManageCommand.builder()
                    .id(null)
                    .roleName("ROLE_CUSTOMER_MANAGER")
                    .description("이벤트관리자")
                    .build();
            Role register = roleManageUseCase.register(command);
            String nameNotExist = "ROLE_PRODUCT_MANAGER";
            boolean existsByName = roleManageUseCase.existsByName(register.getRoleName());
            assertThat(existsByName).isEqualTo(true);

            boolean existsByName1 = roleManageUseCase.existsByName(nameNotExist);
            assertThat(existsByName1).isEqualTo(false);

        }

        @Test
        @DisplayName("성공 케이스인 경우 롤아이디로 조회된 롤을 반환한다.")
        void findById_success_return_role() {
            RoleManageCommand command = RoleManageCommand.builder()
                    .id(null)
                    .roleName("ROLE_CUSTOMER_MANAGER")
                    .description("이벤트관리자")
                    .build();
            Role register = roleManageUseCase.register(command);

            Optional<Role> optionalRole = roleManageUseCase.findById(register.getId());
            assertThat(optionalRole.isPresent()).isEqualTo(true);
        }
    }

    @Nested
    @DisplayName("권한 삭제 시")
    class DeleteTest {
        @Test
        @DisplayName("성공 케이스인 경우 권한이 삭제되고 true를 반환한다.")
        void delete_success_not_found_role() {
            RoleManageCommand command = RoleManageCommand.builder()
                    .id(null)
                    .roleName("ROLE_CUSTOMER_MANAGER")
                    .description("이벤트관리자")
                    .build();

            Role register = roleManageUseCase.register(command);

            roleManageUseCase.delete(register.getId());

            Optional<Role> optionalRole = roleManageUseCase.findByName(register.getRoleName());
            assertThat(optionalRole.isEmpty()).isEqualTo(true);
        }

        @Test
        @DisplayName("삭제할 롤 아이디가 없는 케이스인 경우 false를 반환한다.")
        void delete_fail_when_role_doesnt_exist() {
            assertThat(roleManageUseCase.delete(100L)).isEqualTo(false);
        }
    }
}