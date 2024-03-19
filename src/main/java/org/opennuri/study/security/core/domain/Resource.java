package org.opennuri.study.security.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.codec.ResourceDecoder;

import java.util.Set;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Resource {
    private final Long id;
    private final String resourceName;
    private final String description;
    private final String resourceType;
    private final String httpMethod;
    private final int orderNum;
    private final Set<Role> roleSet;

    public static Resource from(Id id
            , ResourceName resourceName
            , Description description
            , ResourceType resourceType
            , HttpMethod httpMethod
            , OrderNum orderNum
            , Roles roles) {
        return new Resource(id.id, resourceName.resourceName, description.description, resourceType.resourceType
                , httpMethod.httpMethod, orderNum.orderNum, roles.roles);
    }

    public static Resource getEmptyObject() {
        return Resource.from(
                new Resource.Id(0L)
                , new Resource.ResourceName("")
                , new Resource.Description("")
                , new Resource.ResourceType("")
                , new Resource.HttpMethod("")
                , new Resource.OrderNum(0)
                , new Resource.Roles(Set.of(
                        Role.from(
                                new Role.Id(0L)
                                , new Role.RoleName("")
                                , new Role.Description("")
                        )
                ))

        );
    }

    public static class Id {
        private final Long id;
        public Id(Long id) {
            this.id = id;
        }
    }

    public static class ResourceName {
        private final String resourceName;
        public ResourceName(String resourceName) {
            this.resourceName = resourceName;
        }
    }

    public static class Description {
        private final String description;
        public Description(String description) {
            this.description = description;
        }
    }

    public static class ResourceType {
        private final String resourceType;
        public ResourceType(String resourceType) {
            this.resourceType = resourceType;
        }
    }

    public static class HttpMethod {
        private final String httpMethod;
        public HttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
        }
    }

    public static class OrderNum {
        private final int orderNum;
        public OrderNum(int orderNum) {
            this.orderNum = orderNum;
        }
    }

    public static class Roles {
        private final Set<Role> roles;
        public Roles(Set<Role> roles) {
            this.roles = roles;
        }
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", resourceName='" + resourceName + '\'' +
                ", description='" + description + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", orderNum=" + orderNum +
                ", roleSet=" + roleSet +
                '}';
    }
}

