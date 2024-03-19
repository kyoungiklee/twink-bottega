package org.opennuri.study.security.core.application.port.in;

import org.opennuri.study.security.core.domain.Account;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface AccountManageUseCase {

    //Account Update
    Account updateAccount(UpdateAccountCommand command) throws UsernameNotFoundException;

    //Account Delete
    boolean deleteAccount(Long accountId);

    //Account FindAll
    Optional<List<Account>> findAll();

    //Account FindById
    Optional<Account> findById(Long accountId);

    //Account FindByUserName
    Optional<Account> findByUserName(String userName);

    //Account FindByEmail
    Optional<Account> findByEmail(String email);

    //Account FindByUserNameAndEmail
    Optional<Account> findByUserNameAndEmail(String userName, String email);

    // All Account delete
    boolean deleteAll();


}
