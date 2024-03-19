package org.opennuri.study.security.core.application.port.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.opennuri.study.security.core.common.SelfValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Set;


@Getter
@Setter
public class ResourceCommand extends SelfValidation<ResourceCommand> {
    private Long id;

    @NotBlank
    private String resourceName;
    @NotBlank
    private String description;
    @NotBlank
    private String resourceType;
    @NotBlank
    private String httpMethod;
    @PositiveOrZero
    private int orderNum;
    @NotNull
    private Set<String> roles;

    public ResourceCommand(Long id, String resourceName, String description, String resourceType, String httpMethod, int orderNum, Set<String> roles) {
        this.id = id;
        this.resourceName = resourceName;
        this.description = description;
        this.resourceType = resourceType;
        this.httpMethod = httpMethod;
        this.orderNum = orderNum;
        this.roles = roles;
        this.validateSelf();
    }

    @Builder
    public static ResourceCommand updateResourceCommand(Long id, String resourceName, String description, String resourceType, String httpMethod, int orderNum, Set<String> roles) {
        return new ResourceCommand(id, resourceName, description, resourceType, httpMethod, orderNum, roles);
    }

    @Builder
    public static ResourceCommand registerResourceCommand(String resourceName, String description, String resourceType, String httpMethod, int orderNum, Set<String> roles) {
        return new ResourceCommand(null, resourceName, description, resourceType, httpMethod, orderNum, roles);
    }
}
