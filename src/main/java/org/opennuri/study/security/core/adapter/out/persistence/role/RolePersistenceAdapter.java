package org.opennuri.study.security.core.adapter.out.persistence.role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.security.core.application.port.out.RolePersistencePort;
import org.opennuri.study.security.core.common.Adapter;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Adapter
@RequiredArgsConstructor
@Slf4j
public class RolePersistenceAdapter implements RolePersistencePort {

    private final RoleRepository roleRepository;

    @Override
    public Role save(Role role) {

        RoleEntity roleEntity = RoleEntity.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .build();
        return roleRepository.save(roleEntity).toDomain();

    }

    @Override
    public Optional<Role> findByName(String name) {
        log.info("RolePersistenceAdapter.findByName: {}", name);
        Optional<RoleEntity> optional = roleRepository.findByRoleName(name);
        if (optional.isPresent()) {
            return optional.map(RoleEntity::toDomain);
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(Long roleId) {

        Optional<RoleEntity> byId = roleRepository.findById(roleId);
        if (byId.isPresent()) {
            roleRepository.delete(byId.get());
            return true;
        } else {
            log.error("RolePersistenceAdapter.delete-존재하지않는 사용자: {}", roleId);
            return false;
        }
    }

    @Override
    public boolean deleteByName(String name) {
        Optional<RoleEntity> optional = roleRepository.findByRoleName(name);
        if (optional.isPresent()) {
            roleRepository.delete(optional.get());
            return true;
        } else {
            log.error("RolePersistenceAdapter.deleteByName-존재하지않는 사용자: {}", name);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteAll() {

        try {
            roleRepository.deleteAll();
            return true;
        } catch (Exception e) {
            log.error("RolePersistenceAdapter.deleteAll: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existsByName(String name) {
        return roleRepository.existsByRoleName(name);
    }

    @Override
    public Optional<List<Role>> findAll() {
        List<RoleEntity> findAll = roleRepository.findAll();
        if (findAll.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(findAll.stream().map(RoleEntity::toDomain).collect(toList()));
    }

    @Override
    public Optional<List<Role>> findAllById(List<Long> ids) {
        List<RoleEntity> allById = roleRepository.findAllById(ids);
        if (allById.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(allById.stream().map(RoleEntity::toDomain).collect(toList()));
    }

    @Override
    public long count() {
        return roleRepository.count();
    }

    @Override
    public Optional<Role> findById(Long roleId) {
        Optional<RoleEntity> byId = roleRepository.findById(roleId);
        if (byId.isPresent()) {
            return byId.map(RoleEntity::toDomain);
        }
        return Optional.empty();
    }
}
