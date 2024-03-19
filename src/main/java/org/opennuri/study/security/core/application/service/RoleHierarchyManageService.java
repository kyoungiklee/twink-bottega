package org.opennuri.study.security.core.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.security.core.application.port.in.RegisterRoleHierarchyCommand;
import org.opennuri.study.security.core.application.port.in.RoleHierarchyManageUseCase;
import org.opennuri.study.security.core.application.port.in.UpdateRoleHierarchyCommand;
import org.opennuri.study.security.core.application.port.out.RoleHierarchyPersistencePort;
import org.opennuri.study.security.core.common.UseCase;
import org.opennuri.study.security.core.domain.RoleHierarchy;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleHierarchyManageService implements RoleHierarchyManageUseCase {

    private final RoleHierarchyPersistencePort roleHierarchyPersistencePort;


    @Override
    @Transactional
    public RoleHierarchy registerRoleHierarchy(RegisterRoleHierarchyCommand command) {
        RoleHierarchy roleHierarchy = RoleHierarchy.from(
                new RoleHierarchy.Id(null)
                , new RoleHierarchy.Name(command.getName())
                , new RoleHierarchy.ParentName(command.getParentName())
        );
        return roleHierarchyPersistencePort.save(roleHierarchy);
    }


    @Override
    @Transactional
    public RoleHierarchy modifyRoleHierarchy(UpdateRoleHierarchyCommand command) {

        roleHierarchyPersistencePort.findById(command.getId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("%d 권한이 존재하지 않습니다.", command.getId()))
                );

        RoleHierarchy roleHierarchy = RoleHierarchy.from(
                new RoleHierarchy.Id(command.getId())
                , new RoleHierarchy.Name(command.getName())
                , new RoleHierarchy.ParentName(command.getParentName())
        );

        return roleHierarchyPersistencePort.save(roleHierarchy);
    }

    @Override
    @Transactional
    public void removeRoleHierarchy(Long id) {
        roleHierarchyPersistencePort.findById(id).ifPresentOrElse( roleHierarchy ->
                roleHierarchyPersistencePort.delete(roleHierarchy.getId())
        , () -> { throw new IllegalArgumentException(String.format("%d 권한이 존재하지 않습니다.", id));}
        );
    }

    @Override
    @Transactional
    public void removeRoleHierarchy(String name) {
        roleHierarchyPersistencePort.findByName(name).ifPresentOrElse( roleHierarchy ->
                        roleHierarchyPersistencePort.delete(roleHierarchy.getId())
                , () -> { throw new IllegalArgumentException(String.format("%s 권한이 존재하지 않습니다.", name));}
        );

    }

    @Override
    public Optional<RoleHierarchy> findRoleHierarchyByName(String name) {
        return roleHierarchyPersistencePort.findByName(name);
    }

    @Override
    public Optional<RoleHierarchy> findRoleHierarchyById(Long id) {
        return roleHierarchyPersistencePort.findById(id);
    }

    @Override
    public List<RoleHierarchy> findRoleHierarchyByParentName(String parentName) {
        return roleHierarchyPersistencePort.findByParentName(parentName);
    }


    @Override
    public List<RoleHierarchy> findAllRoleHierarchy() {
        return roleHierarchyPersistencePort.findAll();
    }
}
