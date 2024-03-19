package org.opennuri.study.security.core.adapter.out.persistence.account;

import lombok.*;
import org.opennuri.study.security.core.adapter.out.persistence.role.RoleEntity;
import org.opennuri.study.security.core.common.BaseEntity;
import org.opennuri.study.security.core.domain.Account;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@EntityListeners(value = {AccountEntityListener.class})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "ACCOUNT")
@ToString(exclude = "accountRoles")
public class AccountEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "ACCOUNT_ID")
    private Long id;
    @Column(name = "USER_NAME")
    private String username;
    private String password;
    @Column(unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(
            name = "ACCOUNT_ROLE",
            joinColumns = {@JoinColumn(name = "ACCOUNT_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID")}
    )
    private Set<RoleEntity> accountRoles = new HashSet<>();

    public static Account toDomain(AccountEntity accountEntity) {
        return Account.from( new Account.Id(accountEntity.id),
                new Account.Username(accountEntity.username),
                new Account.Password(accountEntity.password),
                new Account.Email(accountEntity.email),
                new Account.Roles(accountEntity.accountRoles.stream().map(RoleEntity::toDomain).collect(Collectors.toSet())));
    }
}
