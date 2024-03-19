package org.opennuri.study.security.core.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.security.core.application.port.in.AccountManageUseCase;
import org.opennuri.study.security.core.application.port.in.UpdateAccountCommand;
import org.opennuri.study.security.core.application.port.out.AccountPersistencePort;
import org.opennuri.study.security.core.application.port.out.RolePersistencePort;
import org.opennuri.study.security.core.common.UseCase;
import org.opennuri.study.security.core.domain.Account;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountManageService implements AccountManageUseCase {
    private final AccountPersistencePort accountPersistencePort;
    private final RolePersistencePort rolePersistencePort;

    @Override
    @Transactional
    public Account updateAccount(UpdateAccountCommand command) {
        Optional<Account> optionalAccount = findById(command.getId());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            Account updateAccount = Account.from(
                    new Account.Id(account.getId())
                    , new Account.Username(account.getUsername())
                    , new Account.Password(account.getPassword())
                    , new Account.Email(account.getEmail())
                    , new Account.Roles(command.getRoles().stream()
                            .map(role -> rolePersistencePort.findByName(role)
                                    .orElseThrow(IllegalArgumentException::new))
                            .collect(Collectors.toSet())
                    ));
            return accountPersistencePort.update(updateAccount);
        } else {
            throw new UsernameNotFoundException("사용자 정보가 존재하지 않습니다.");
        }
    }

    @Override
    @Transactional
    public boolean deleteAccount(Long accountId) {
        return accountPersistencePort.delete(accountId);
    }

    @Override
    public Optional<List<Account>> findAll() {
        return accountPersistencePort.findAll();
    }

    @Override
    public Optional<Account> findById(Long accountId) {
        return accountPersistencePort.findById(accountId);
    }

    @Override
    public Optional<Account> findByUserName(String userName) {
        return accountPersistencePort.findByUsername(userName);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountPersistencePort.findByEmail(email);
    }

    @Override
    public Optional<Account> findByUserNameAndEmail(String userName, String email) {
        return accountPersistencePort.findByUsernameAndEmail(userName, email);
    }

    @Override
    @Transactional
    public boolean deleteAll() {
        return accountPersistencePort.deleteAll();
    }
}
