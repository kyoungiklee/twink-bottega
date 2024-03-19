package org.opennuri.study.security.core.adapter.out.persistence.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.security.core.adapter.out.persistence.role.RoleRepository;
import org.opennuri.study.security.core.application.port.out.AccountPersistencePort;
import org.opennuri.study.security.core.common.Adapter;
import org.opennuri.study.security.core.domain.Account;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Adapter
@RequiredArgsConstructor
@Slf4j
public class AccountPersistenceAdapter implements AccountPersistencePort {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    @Override
    public Account save(Account account) {
        AccountEntity save = accountRepository.save(AccountEntity.builder()
                .username(account.getUsername())
                .email(account.getEmail())
                .password(account.getPassword())
                .accountRoles(
                        account.getRoles().stream()
                                .map(role -> roleRepository.findByRoleName(role.getRoleName())
                                        .orElseThrow(() -> new IllegalArgumentException("RoleDto not found")))
                                .collect(Collectors.toSet())
                )
                .build());
        return AccountEntity.toDomain(save);
    }

    @Override
    public Optional<Account> findByName(String username) {
        Optional<AccountEntity> optionalAccount = accountRepository.findByUsername(username);
        return optionalAccount.map(AccountEntity::toDomain);
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        Optional<AccountEntity> optionalAccount = accountRepository.findByUsername(username);
        return optionalAccount.map(AccountEntity::toDomain);
    }

    @Override
    public Account update(Account account) {
        AccountEntity save = accountRepository.save(AccountEntity.builder()
                .id(account.getId())
                .username(account.getUsername())
                .email(account.getEmail())
                .password(account.getPassword())
                .accountRoles(
                        account.getRoles().stream()
                                .map(role -> roleRepository.findByRoleName(role.getRoleName())
                                        .orElseThrow(() -> new IllegalArgumentException("RoleDto not found")))
                                .collect(Collectors.toSet())
                )
                .build());
        return AccountEntity.toDomain(save);
    }

    @Override
    public boolean delete(Long accountId) {
        try {
            accountRepository.deleteById(accountId);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public Optional<List<Account>> findAll() {
        List<AccountEntity> all = accountRepository.findAll();
        if (all.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(all.stream().map(AccountEntity::toDomain).collect(Collectors.toList()));
    }

    @Override
    public Optional<Account> findById(Long accountId) {
        return accountRepository.findById(accountId)
                .map(AccountEntity::toDomain)
                .or(Optional::empty);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(email)
                .map(AccountEntity::toDomain)
                .or(Optional::empty);

        return optionalAccount;
    }

    @Override
    public Optional<Account> findByUsernameAndEmail(String userName, String email) {
        return accountRepository.findByUsernameAndEmail(userName, email)
                .map(AccountEntity::toDomain)
                .or(Optional::empty);
    }

    @Override
    public boolean deleteAll() {
        try {
            accountRepository.deleteAll();
            return true;
        } catch (Exception e) {
            log.error("Account deleteAll error: {}", e.getMessage());
            return false;
        }
    }
}

