package org.opennuri.study.security.core.adapter.out.persistence.role;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.security.core.application.port.out.RoleHierarchyPersistencePort;
import org.opennuri.study.security.core.common.Adapter;
import org.opennuri.study.security.core.domain.RoleHierarchy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Adapter
@RequiredArgsConstructor
public class RoleHierarchyPersistenceAdapter implements RoleHierarchyPersistencePort {
    private final RoleHierarchyRepository repository;


    @Override
    public RoleHierarchy save(RoleHierarchy roleHierarchy) {
        AtomicReference<RoleHierarchyEntity> savedEntity = new AtomicReference<>();
        Optional.ofNullable(roleHierarchy.getId()).ifPresentOrElse(
                id -> {
                    RoleHierarchyEntity entity = repository.findById(id).orElseThrow();
                    entity.setName(roleHierarchy.getName());
                    entity.setParent(repository.findByName(roleHierarchy.getParentName()).orElse(null));
                    savedEntity.set(repository.save(entity));
                },
                () -> {
                    RoleHierarchyEntity parent = repository.findByName(roleHierarchy.getParentName()).orElse(null);
                    RoleHierarchyEntity entity = RoleHierarchyEntity.builder()
                            .name(roleHierarchy.getName())
                            .parent(parent)
                            .build();
                    savedEntity.set(repository.save(entity));
                }
        );
        return savedEntity.get().toDomain();
    }

    @Override
    public Optional<RoleHierarchy> findById(Long id) {
        return repository.findById(id).map(RoleHierarchyEntity::toDomain);
    }

    @Override
    public Optional<RoleHierarchy> findByName(String name) {
        return repository.findByName(name).map(RoleHierarchyEntity::toDomain);
    }

    @Override
    public List<RoleHierarchy> findByParentName(String parentName) {

        return repository.findByParentName(parentName).stream()
                .map(RoleHierarchyEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleHierarchy> findAll() {
        return repository.findAll().stream()
                .map(RoleHierarchyEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
