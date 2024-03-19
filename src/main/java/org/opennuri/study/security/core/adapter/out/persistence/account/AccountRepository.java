package org.opennuri.study.security.core.adapter.out.persistence.account;

import org.opennuri.study.security.core.adapter.out.persistence.account.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByUsername(String name);

    Optional<AccountEntity> findByEmail(String email);

    Optional<AccountEntity> findByUsernameAndEmail(String userName, String email);
}
