package org.opennuri.study.security.core.adapter.in.web.front.membership;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.security.core.application.port.in.RegisterAccountCommand;
import org.opennuri.study.security.core.application.port.in.RegisterAccountUseCase;
import org.opennuri.study.security.core.domain.Account;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RegisterAccountController {
    private final RegisterAccountUseCase registerAccountUseCase;
    @GetMapping("/register")
    public String register() {
        return "front/membership/registerForm";
    }

    @PostMapping("/register")
    public String registerProcess(RegisterUserRequest accountRequest) {
        log.info("accountRequest: {}", accountRequest);

        RegisterAccountCommand command = RegisterAccountCommand.builder()
                .name(accountRequest.getName())
                .password(accountRequest.getPassword())
                .email(accountRequest.getEmail())
                .roles(Set.of(Role.from(
                        new Role.Id(null),
                        new Role.RoleName("ROLE_USER"),
                        new Role.Description("일반 사용자")
                )))
                .build();

        Account register = registerAccountUseCase.register(command);
        log.info("register: {}", register);

        return "redirect:/";
    }
}
