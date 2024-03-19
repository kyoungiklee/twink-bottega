package org.opennuri.study.security.core.adapter.out.persistence.role;

import lombok.*;
import org.opennuri.study.security.core.common.BaseEntity;
import org.opennuri.study.security.core.domain.RoleHierarchy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "ROLE_HIERARCHY")
@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"roleHierarchies", "parent"})
public class RoleHierarchyEntity extends BaseEntity implements Serializable {

    @Id @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "PARENT_NAME", referencedColumnName = "NAME", columnDefinition = "")
    private RoleHierarchyEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<RoleHierarchyEntity> roleHierarchies;

    public RoleHierarchy toDomain() {
        return RoleHierarchy.from(
                new RoleHierarchy.Id(id)
                , new RoleHierarchy.Name(name)
                , new RoleHierarchy.ParentName(parent == null ? "" : parent.getName())
        );
    }
}
