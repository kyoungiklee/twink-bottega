package org.opennuri.study.security.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RoleHierarchy {

    private Long id; // 아이디

    private String name; // 자식 권한명

    private String parentName; // 부모 권한


    public static class Id {
        private final Long id;

        public Id(Long id) {
            this.id = id;
        }
    }
    public static RoleHierarchy from(Id id, Name name, ParentName parentName) {
        return new RoleHierarchy(id.id, name.name, parentName.parentName);
    }
    public static class Name {
        private final String name;

        public Name(String name) {
            this.name = name;
        }
    }

    public static class ParentName {
        private final String parentName;

        public ParentName(String parentName) {
            this.parentName = parentName;
        }
    }
}
