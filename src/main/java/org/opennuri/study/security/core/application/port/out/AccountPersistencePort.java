package org.opennuri.study.security.core.application.port.out;

import org.opennuri.study.security.core.domain.Account;

import java.util.List;
import java.util.Optional;

public interface AccountPersistencePort {
    Account save(Account account);

    Optional<Account> findByName(String admin);

    Optional<Account> findByUsername(String username);

    Account update(Account account);

    //Account Delete
    boolean delete(Long accountId);

    //Account FindAll
    Optional<List<Account>> findAll();

    //Account FindById
    Optional<Account> findById(Long accountId);


    //Account FindByEmail
    Optional<Account> findByEmail(String email);

    //Account FindByUserNameAndEmail
    Optional<Account> findByUsernameAndEmail(String userName, String email);

    // All Account delete
    boolean deleteAll();
}
