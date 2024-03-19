package org.opennuri.study.security.core.application.security.listener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.opennuri.study.security.core.application.port.in.*;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
@Component
@RequiredArgsConstructor
@Profile("!test")
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final AccountManageUseCase accountManageUseCase;
    private final RegisterAccountUseCase registerAccountUseCase;
    private final RoleManageUseCase roleManageUseCase;
    private final ResourceManageUseCase resourceManageUseCase;


    private static RegisterAccountCommand getRegisterAccountCommand(String name, String password, String email, Role role) {
        return RegisterAccountCommand.builder()
                .name(name)
                .password(password)
                .email(email)
                .roles(Set.of(role))
                .build();
    }

    private static RoleManageCommand getRoleManageCommand(String roleName, String description) {
        return RoleManageCommand.builder()
                .roleName(roleName)
                .description(description)
                .build();
    }

    @Override
    @Transactional
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {

        setupSecurityData();
        createRoleHierarchy();
    }

    private void createRoleHierarchy() {

    }

    private void setupSecurityData() {
        //role 등록
        roleManageUseCase.findByName("ROLE_ADMIN").ifPresentOrElse(
                role -> {}
                , () -> roleManageUseCase.register(getRoleManageCommand("ROLE_ADMIN", "관리자 권한"))
        );

        roleManageUseCase.findByName("ROLE_USER").ifPresentOrElse(
                role -> {}
                , () -> roleManageUseCase.register(getRoleManageCommand("ROLE_USER", "일반 사용자 권한"))
        );



        roleManageUseCase.findByName("ROLE_MANAGER").ifPresentOrElse(
                role -> {}
                , () -> roleManageUseCase.register(getRoleManageCommand("ROLE_MANAGER", "매니저 권한"))
        );

        //account 등록
        accountManageUseCase.findByEmail("mail+1@gmail.com").ifPresentOrElse(
                account -> {}
                , () -> registerAccountUseCase.register(getRegisterAccountCommand("admin", "admin", "mail+1@gmail.com", Role.from(
                        new Role.Id(null)
                        , new Role.RoleName("ROLE_ADMIN")
                        , new Role.Description("관리자 권한")
                )))
        );

        accountManageUseCase.findByEmail("mail+2@gmail.com").ifPresentOrElse(
                account -> {}
                , () -> registerAccountUseCase.register(getRegisterAccountCommand("user", "user", "mail+2@gmail.com", Role.from(
                        new Role.Id(null)
                        , new Role.RoleName("ROLE_USER")
                        , new Role.Description("일반 사용자 권한")
                )))
        );

        accountManageUseCase.findByEmail("mail+3@gmail.com").ifPresentOrElse(
                account -> {}
                , () -> registerAccountUseCase.register(getRegisterAccountCommand("manager", "manager", "mail+3@gmail.com", Role.from(
                        new Role.Id(null)
                        , new Role.RoleName("ROLE_MANAGER")
                        , new Role.Description("매니저 권한")
                )))
        );

        //resource 등록
        resourceManageUseCase.findByResourceName("/mypage").ifPresentOrElse(
                resource -> {}
                , () -> resourceManageUseCase.createResource(RegisterResourceCommand.builder()
                        .resourceName("/mypage")
                        .description("사용자 정보")
                        .httpMethod("GET")
                        .orderNum(1)
                        .resourceType("url")
                        .roles(Set.of("ROLE_USER"))
                        .build()));

        resourceManageUseCase.findByResourceName("/messages").ifPresentOrElse(
                resource -> {}
                , () -> resourceManageUseCase.createResource(RegisterResourceCommand.builder()
                        .resourceName("/messages")
                        .description("메시지 정보")
                        .httpMethod("GET")
                        .orderNum(2)
                        .resourceType("url")
                        .roles(Set.of("ROLE_MANAGER"))
                        .build()));

        resourceManageUseCase.findByResourceName("/config").ifPresentOrElse(
                resource -> {}
                , () -> resourceManageUseCase.createResource(RegisterResourceCommand.builder()
                        .resourceName("/config")
                        .description("설정 정보")
                        .httpMethod("GET")
                        .orderNum(3)
                        .resourceType("url")
                        .roles(Set.of("ROLE_ADMIN"))
                        .build()));
    }
}