package org.opennuri.study.security.core.adapter.out.persistence.account.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountHistoryRepository extends JpaRepository<AccountHistoryEntity, Long> {

    Optional<AccountHistoryEntity> findByAccountId(Long id);
}
