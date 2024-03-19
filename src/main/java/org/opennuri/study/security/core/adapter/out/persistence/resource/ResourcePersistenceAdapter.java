package org.opennuri.study.security.core.adapter.out.persistence.resource;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.security.core.adapter.out.persistence.role.RoleRepository;
import org.opennuri.study.security.core.application.port.out.ResourcePersistencePort;
import org.opennuri.study.security.core.common.Adapter;
import org.opennuri.study.security.core.domain.Resource;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Adapter
@RequiredArgsConstructor
public class ResourcePersistenceAdapter implements ResourcePersistencePort {
    private final ResourceRepository resourceRepository;
    private final RoleRepository roleRepository;

    @Override
    public Resource createResource(Resource resource) {
        AtomicReference<ResourceEntity> atomicReference = new AtomicReference<>();

        ResourceEntity resourceEntity = ResourceEntity.builder()
                .resourceName(resource.getResourceName())
                .resourceType(resource.getResourceType())
                .description(resource.getDescription())
                .httpMethod(resource.getHttpMethod())
                .orderNum(resource.getOrderNum())
                .resourceRoles(resource.getRoleSet().stream()
                        .map(role -> roleRepository.findByRoleName(role.getRoleName())
                                .orElseThrow()).collect(Collectors.toSet()))
                .build();
        atomicReference.set(resourceRepository.save(resourceEntity));
        return atomicReference.get().toDomain();
    }

    @Override
    public Resource updateResource(Resource resource) {
        //AtomicReference is used to update the value of a variable in a thread-safe way
        AtomicReference<Resource> updatedResource = new AtomicReference<>();
        resourceRepository.findById(resource.getId()).ifPresent(resourceEntity -> {
            resourceEntity.setResourceName(resource.getResourceName());
            resourceEntity.setResourceType(resource.getResourceType());
            resourceEntity.setDescription(resource.getDescription());
            resourceEntity.setHttpMethod(resource.getHttpMethod());
            resourceEntity.setOrderNum(resource.getOrderNum());
            resourceEntity.setResourceRoles(resource.getRoleSet().stream()
                    .map(role -> roleRepository.findByRoleName(role.getRoleName())
                            .orElseThrow()).collect(Collectors.toSet()));
            updatedResource.set(resourceRepository.save(resourceEntity).toDomain());
        });
        return updatedResource.get();
    }

    @Override
    public boolean deleteResource(Long resourceId) {
        try {
            resourceRepository.deleteById(resourceId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<Resource> findById(Long resourceId) {
        Optional<ResourceEntity> optionalResourceEntity = resourceRepository.findById(resourceId);
        return optionalResourceEntity.map(ResourceEntity::toDomain);
    }

    @Override
    public Optional<Resource> findByResourceName(String resourceName) {
        return resourceRepository.findByResourceName(resourceName).map(ResourceEntity::toDomain);
    }

    @Override
    public boolean deleteAll() {
        try {
            resourceRepository.deleteAll();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<List<Resource>> findAll() {
        List<ResourceEntity> findAll = resourceRepository.findAll();
        if (findAll.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(findAll.stream()
                .map(ResourceEntity::toDomain)
                .sorted((o1, o2) -> (int) (o1.getOrderNum() - o2.getOrderNum()))
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<Resource>> findAllResources() {

        Optional<List<ResourceEntity>> allResources = resourceRepository.findAllResources();

        return allResources.map(resourceEntities -> resourceEntities.stream()
                .map(ResourceEntity::toDomain)
                .collect(Collectors.toList()));
    }
}
