package org.opennuri.study.security.core.application.port.out;

import org.opennuri.study.security.core.domain.RoleHierarchy;

import java.util.List;
import java.util.Optional;

public interface RoleHierarchyPersistencePort {

    RoleHierarchy save(RoleHierarchy roleHierarchy);

    Optional<RoleHierarchy> findById(Long id);

    Optional<RoleHierarchy> findByName(String childName);

    List<RoleHierarchy> findByParentName(String parentName);

    List<RoleHierarchy> findAll();

    void deleteAll();

    void delete(Long id);
}
