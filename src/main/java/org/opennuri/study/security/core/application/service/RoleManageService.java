package org.opennuri.study.security.core.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.security.core.application.port.in.RoleManageCommand;
import org.opennuri.study.security.core.application.port.in.RoleManageUseCase;
import org.opennuri.study.security.core.application.port.out.RolePersistencePort;
import org.opennuri.study.security.core.common.UseCase;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleManageService implements RoleManageUseCase {
    private final RolePersistencePort rolePersistencePort;
    @Override
    @Transactional
    public Role register(RoleManageCommand command) {
        Role role = Role.from(
                new Role.Id(command.getId())
                , new Role.RoleName(command.getRoleName())
                , new Role.Description(command.getDescription())
        );

        return rolePersistencePort.save(role);
    }


    @Override
    public Optional<Role> findByName(String roleName) {
        return rolePersistencePort.findByName(roleName);
    }

    @Override
    @Transactional
    public boolean delete(Long roleId) {
        return rolePersistencePort.delete(roleId);
    }

    @Override
    @Transactional
    public boolean deleteByName(String roleName) {
        return rolePersistencePort.deleteByName(roleName);
    }

    @Override
    @Transactional
    public boolean deleteAll() {
        return rolePersistencePort.deleteAll();

    }

    @Override
    public boolean existsByName(String roleName) {
        return rolePersistencePort.existsByName(roleName);
    }

    @Override
    public Optional<List<Role>> findAll() {
        return rolePersistencePort.findAll();
    }

    @Override
    public Optional<List<Role>> findAllById(List<Long> ids) {
        return rolePersistencePort.findAllById(ids);
    }

    @Override
    public long count() {
        return rolePersistencePort.count();
    }

    @Override
    public Optional<Role> findById(Long roleId) {
        return rolePersistencePort.findById(roleId);
    }


}
