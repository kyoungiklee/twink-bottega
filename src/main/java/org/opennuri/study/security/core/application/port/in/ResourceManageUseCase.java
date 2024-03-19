package org.opennuri.study.security.core.application.port.in;

import org.opennuri.study.security.core.domain.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourceManageUseCase {
    Resource createResource(RegisterResourceCommand resource);
    Resource updateResource(UpdateResourceCommand resource);
    boolean deleteResource(Long resourceId);
    Optional<Resource> findById(Long resourceId);
    Optional<Resource> findByResourceName(String resourceName);
    boolean deleteAll();
    Optional<List<Resource>> findAll();
}
