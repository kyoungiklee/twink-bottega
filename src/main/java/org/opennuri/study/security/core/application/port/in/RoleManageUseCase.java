package org.opennuri.study.security.core.application.port.in;

import org.opennuri.study.security.core.domain.Role;

import java.util.List;
import java.util.Optional;

public interface RoleManageUseCase {
    Role register(RoleManageCommand command);

    Optional<Role> findByName(String roleName);

    boolean delete(Long roleId);

    boolean deleteByName(String roleName);

    boolean deleteAll();

    boolean existsByName(String roleName);

    Optional<List<Role>> findAll();

    Optional<List<Role>> findAllById(List<Long> ids);

    long count();

    Optional<Role> findById(Long roleId);
}
