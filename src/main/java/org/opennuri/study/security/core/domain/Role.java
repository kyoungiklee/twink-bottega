package org.opennuri.study.security.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Role {
    private final Long id;
    private final String roleName;
    private final String description;

    public static Role from(Id id, RoleName roleName, Description description) {
        return new Role(id.id, roleName.roleName, description.description);
    }

    public static Role getEmptyObject() {
        return Role.from(
                new Id(0L)
                , new RoleName("")
                , new Description("")
        );
    }

    public static class Id {
        private final Long id;
        public Id(Long id) {
            this.id = id;
        }
    }

    public static class RoleName {
        private final String roleName;
        public RoleName(String roleName) {
            this.roleName = roleName;
        }
    }

    public static class Description {
        private final String description;
        public Description(String description) {
            this.description = description;
        }
    }

    @Override
    public String toString() {
        return "RoleDto{" +
                "id=" + id +
                ", name='" + roleName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
