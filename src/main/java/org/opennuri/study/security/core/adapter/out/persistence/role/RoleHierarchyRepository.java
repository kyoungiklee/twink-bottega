package org.opennuri.study.security.core.adapter.out.persistence.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchyEntity, Long> {
    Optional<RoleHierarchyEntity> findByName(String name);

    List<RoleHierarchyEntity> findByParentName(String parentName);
}
