package org.opennuri.study.security.core.adapter.out.persistence.role;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class RoleHierarchyEntityTest {

    @Autowired
    RoleHierarchyRepository roleHierarchyRepository;

    private RoleHierarchyEntity savedManagerRoleHierarchyEntity;

    private RoleHierarchyEntity savedUserRoleHierarchyEntity;

    @BeforeEach
    void setUp() {
        //given
        RoleHierarchyEntity adminRoleHierarchy = RoleHierarchyEntity.builder()
                .name("ROLE_ADMIN")
                .build();

        RoleHierarchyEntity savedAdminRoleHierarchyEntity = roleHierarchyRepository.save(adminRoleHierarchy);

        RoleHierarchyEntity managerRoleHierarchyEntity = RoleHierarchyEntity.builder()
                .name("ROLE_MANAGER")
                .parent(savedAdminRoleHierarchyEntity)
                .build();

        savedManagerRoleHierarchyEntity = roleHierarchyRepository.save(managerRoleHierarchyEntity);

        RoleHierarchyEntity userRoleHierarchyEntity = RoleHierarchyEntity.builder()
                .name("ROLE_USER")
                .parent(savedManagerRoleHierarchyEntity)
                .build();
        savedUserRoleHierarchyEntity = roleHierarchyRepository.save(userRoleHierarchyEntity);
    }
    @AfterEach
    void tearDown() {
        roleHierarchyRepository.deleteAll();
    }
    @Test
    @DisplayName("RoleHierarchyEntity 등록 테스트")
    void saveRoleHierarchyEntity() {

        // when
        Optional<RoleHierarchyEntity> optionalManagerRoleHierarchy
                = roleHierarchyRepository.findById(savedManagerRoleHierarchyEntity.getId());

        Optional<RoleHierarchyEntity> optionalUserRoleHierarchy
                = roleHierarchyRepository.findById(savedUserRoleHierarchyEntity.getId());

        // then
        assertThat(optionalManagerRoleHierarchy.isPresent()).isTrue();
        assertThat(optionalManagerRoleHierarchy.get().getName()).isEqualTo("ROLE_MANAGER");
        assertThat(optionalManagerRoleHierarchy.get().getParent().getName()).isEqualTo("ROLE_ADMIN");

        assertThat(optionalUserRoleHierarchy.isPresent()).isTrue();
        assertThat(optionalUserRoleHierarchy.get().getName()).isEqualTo("ROLE_USER");
        assertThat(optionalUserRoleHierarchy.get().getParent().getName()).isEqualTo("ROLE_MANAGER");
    }
}