package org.opennuri.study.security.core.adapter.out.persistence.role;

import lombok.*;
import org.opennuri.study.security.core.adapter.out.persistence.account.AccountEntity;
import org.opennuri.study.security.core.adapter.out.persistence.resource.ResourceEntity;
import org.opennuri.study.security.core.common.BaseEntity;
import org.opennuri.study.security.core.domain.Role;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "ROLE")
@ToString(exclude = {"accountRoleSet", "resourceRoleSet"})
public class RoleEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "ROLE_ID")
    private Long id;
    @Column(name = "ROLE_NAME")
    private String roleName;
    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "accountRoles", fetch = FetchType.LAZY)
    private Set<AccountEntity> accountRoleSet;

    @ManyToMany(mappedBy = "resourceRoles", fetch = FetchType.LAZY)
    private Set<ResourceEntity> resourceRoleSet;

    public Role toDomain() {
        return Role.from(
                new Role.Id(this.id),
                new Role.RoleName(this.roleName),
                new Role.Description(this.description)
        );
    }
}
