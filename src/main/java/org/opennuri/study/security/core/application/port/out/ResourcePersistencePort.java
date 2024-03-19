package org.opennuri.study.security.core.application.port.out;

import org.opennuri.study.security.core.domain.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourcePersistencePort {
    Resource createResource(Resource resource);
    Resource updateResource(Resource resource);
    boolean deleteResource(Long resourceId);
    Optional<Resource> findById(Long resourceId);
    Optional<Resource> findByResourceName(String resourceName);
    boolean deleteAll();
    Optional<List<Resource>> findAll();

    Optional<List<Resource>> findAllResources();
}
