package org.opennuri.study.security.core.adapter.out.persistence.resource;

import lombok.*;
import org.opennuri.study.security.core.adapter.out.persistence.role.RoleEntity;
import org.opennuri.study.security.core.common.BaseEntity;
import org.opennuri.study.security.core.domain.Resource;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "RESOURCE")
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = "resourceRoles")
public class ResourceEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "RESOURCE_ID")
    private Long id;
    @Column(name = "RESOURCE_NAME")
    private String resourceName;
    @Column(name = "HTTP_METHOD")
    private String httpMethod;
    @Column(name = "RESOURCE_TYPE")
    private String resourceType;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "ORDER_NUM")
    private int orderNum;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "RESOURCE_ROLE",
            joinColumns = {@JoinColumn(name = "RESOURCE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID")}
    )
    private Set<RoleEntity> resourceRoles = new HashSet<>();

    public Resource toDomain() {
        return Resource.from(
                new Resource.Id(this.id),
                new Resource.ResourceName(this.resourceName),
                new Resource.Description(this.description),
                new Resource.ResourceType(this.resourceType),
                new Resource.HttpMethod(this.httpMethod),
                new Resource.OrderNum(this.orderNum),
                new Resource.Roles(this.resourceRoles.stream().map(RoleEntity::toDomain).collect(Collectors.toSet()))
        );
    }
}
