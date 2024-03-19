package org.opennuri.study.security.core.adapter.out.persistence.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceEntity, Long> {
    Optional<ResourceEntity> findByResourceName(String resourceName);

    @Query("select r from ResourceEntity r join fetch r.resourceRoles where r.resourceType = 'url' order by r.orderNum asc")
    Optional<List<ResourceEntity>> findAllResources();
}
