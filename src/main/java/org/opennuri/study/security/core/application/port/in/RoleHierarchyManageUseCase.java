package org.opennuri.study.security.core.application.port.in;

import org.opennuri.study.security.core.domain.RoleHierarchy;

import java.util.List;
import java.util.Optional;

public interface RoleHierarchyManageUseCase {

    RoleHierarchy registerRoleHierarchy(RegisterRoleHierarchyCommand roleHierarchy);

    RoleHierarchy modifyRoleHierarchy(UpdateRoleHierarchyCommand roleHierarchy);

    void removeRoleHierarchy(Long id);

    void removeRoleHierarchy(String name);

    Optional<RoleHierarchy> findRoleHierarchyById(Long id);

    List<RoleHierarchy> findRoleHierarchyByParentName(String parentName);

    Optional<RoleHierarchy> findRoleHierarchyByName(String name);

    List<RoleHierarchy> findAllRoleHierarchy();
}
