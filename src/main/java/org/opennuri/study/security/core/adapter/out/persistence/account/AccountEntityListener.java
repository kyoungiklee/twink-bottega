package org.opennuri.study.security.core.adapter.out.persistence.account;

import org.opennuri.study.security.core.adapter.out.persistence.account.history.AccountHistoryEntity;
import org.opennuri.study.security.core.adapter.out.persistence.account.history.AccountHistoryRepository;
import org.opennuri.study.security.core.common.BeanUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PreUpdate;

public class AccountEntityListener {
    @PostPersist
    @PostUpdate
    @PreUpdate
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void prePersistAndUpdate(Object o) {
        AccountEntity accountEntity = (AccountEntity) o;

        AccountHistoryEntity.builder()
                .accountId(accountEntity.getId())
                .username(accountEntity.getUsername())
                .email(accountEntity.getEmail())
                .password(accountEntity.getPassword())
                .roles(accountEntity.getAccountRoles().toString())
                .createdDateTime(accountEntity.getCreatedAt())
                .createdUser(accountEntity.getCreatedBy())
                .modifiedDateTime(accountEntity.getModifiedAt())
                .modifiedUser(accountEntity.getModifiedBy())
                .build();

        AccountHistoryRepository accountHistoryRepository = BeanUtils.getBean(AccountHistoryRepository.class);

        accountHistoryRepository.save(AccountHistoryEntity.builder()
                .accountId(accountEntity.getId())
                .username(accountEntity.getUsername())
                .email(accountEntity.getEmail())
                .password(accountEntity.getPassword())
                .roles(accountEntity.getAccountRoles().toString())
                .createdDateTime(accountEntity.getCreatedAt())
                .createdUser(accountEntity.getCreatedBy())
                .modifiedDateTime(accountEntity.getModifiedAt())
                .modifiedUser(accountEntity.getModifiedBy())
                .build());
    }
}
