package org.opennuri.study.security.core.application.port.out;

import org.opennuri.study.security.core.domain.Role;

import java.util.List;
import java.util.Optional;

public interface RolePersistencePort {
    Role save(Role role);
    Optional<Role> findByName(String name);
    boolean delete(Long role);
    boolean deleteByName(String name);
    boolean deleteAll();
    boolean existsByName(String name);
    Optional<List<Role>> findAll();
    Optional<List<Role>> findAllById(List<Long> ids);
    long count();

    Optional<Role> findById(Long roleId);
}
