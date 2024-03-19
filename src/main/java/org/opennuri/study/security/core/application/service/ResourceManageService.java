package org.opennuri.study.security.core.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.security.core.application.port.in.*;
import org.opennuri.study.security.core.application.port.out.ResourcePersistencePort;
import org.opennuri.study.security.core.application.security.metadatasource.UrlFilterInvocationSecurityMetadatasource;
import org.opennuri.study.security.core.application.security.srevice.SecurityResourceService;
import org.opennuri.study.security.core.common.UseCase;
import org.opennuri.study.security.core.domain.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceManageService implements ResourceManageUseCase {
    private final RoleManageUseCase roleManageUseCase;
    private final ResourcePersistencePort resourcePersistencePort;
    private final SecurityResourceService securityResourceService;

    @Override
    @Transactional
    public Resource createResource(RegisterResourceCommand command) {

        Resource resource = Resource.from(
                new Resource.Id(null)
                , new Resource.ResourceName(command.getResourceName())
                , new Resource.Description(command.getDescription())
                , new Resource.ResourceType(command.getResourceType())
                , new Resource.HttpMethod(command.getHttpMethod())
                , new Resource.OrderNum(command.getOrderNum())
                , new Resource.Roles(
                        command.getRoles().stream().map(roleManageUseCase::findByName)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toSet())
                )
        );
        return resourcePersistencePort.createResource(resource);
    }

    @Override
    @Transactional
    public Resource updateResource(UpdateResourceCommand command) {
        Resource resource = Resource.from(
                new Resource.Id(command.getId())
                , new Resource.ResourceName(command.getResourceName())
                , new Resource.Description(command.getDescription())
                , new Resource.ResourceType(command.getResourceType())
                , new Resource.HttpMethod(command.getHttpMethod())
                , new Resource.OrderNum(command.getOrderNum())
                , new Resource.Roles(
                        command.getRoles().stream().map(roleManageUseCase::findByName)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toSet())
                )
        );

        Resource updatedResource = resourcePersistencePort.updateResource(resource);
        securityResourceService.reloadMetaData();
        return updatedResource;
    }

    @Override
    @Transactional
    public boolean deleteResource(Long resourceId) {
        Optional<Resource> optionalResource = resourcePersistencePort.findById(resourceId);

        AtomicBoolean isSuccess = new AtomicBoolean(false);
        optionalResource.ifPresent(resource -> {
            boolean succeed = resourcePersistencePort.deleteResource(resource.getId());
            isSuccess.set(succeed);
                }
        );
        securityResourceService.reloadMetaData();
        return isSuccess.get();
    }

    @Override
    public Optional<Resource> findById(Long resourceId) {
        return resourcePersistencePort.findById(resourceId);
    }

    @Override
    public Optional<Resource> findByResourceName(String resourceName) {
        return resourcePersistencePort.findByResourceName(resourceName);
    }

    @Override
    @Transactional
    public boolean deleteAll() {
        return resourcePersistencePort.deleteAll();
    }

    @Override
    public Optional<List<Resource>> findAll() {
        return resourcePersistencePort.findAll();
    }
}
