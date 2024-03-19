package org.opennuri.study.security.core.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.security.core.application.port.in.RegisterAccountCommand;
import org.opennuri.study.security.core.application.port.in.RegisterAccountUseCase;
import org.opennuri.study.security.core.application.port.out.AccountPersistencePort;
import org.opennuri.study.security.core.common.UseCase;
import org.opennuri.study.security.core.domain.Account;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegisterAccountService implements RegisterAccountUseCase {
    private final AccountPersistencePort accountPersistencePort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Account register(RegisterAccountCommand command) {
        
        Account account = Account.from(
                new Account.Id(null) // 사용자 등록 시 id는 null;
                , new Account.Username(command.getName())
                , new Account.Password(passwordEncoder.encode(command.getPassword())) // password encoding
                , new Account.Email(command.getEmail())
                //command에 role 존재하는 경우 role의 값을 사용하여 등록하고 없는 경우 default(ROLE_USER) 등록
                , new Account.Roles(Optional.ofNullable(command.getRoles())
                        .orElseGet(RegisterAccountService::gerDefaultRole)
                )
        );

        accountPersistencePort.findByEmail(command.getEmail()).ifPresent(a -> {
            throw new DuplicateKeyException("이미 가입된 이메일 주소입니다.");
        });

        return accountPersistencePort.save(account);
    }

    private static Set<Role> gerDefaultRole() {
        return Set.of(Role.from(
                new Role.Id(null)
                , new Role.RoleName("ROLE_USER")
                , new Role.Description("사용자 권한")
        ));
    }

}
