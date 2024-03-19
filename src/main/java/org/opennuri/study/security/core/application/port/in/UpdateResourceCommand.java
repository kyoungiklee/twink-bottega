package org.opennuri.study.security.core.application.port.in;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.opennuri.study.security.core.common.SelfValidation;
import org.opennuri.study.security.core.domain.Resource;
import org.opennuri.study.security.core.domain.Role;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@Builder
public class UpdateResourceCommand extends SelfValidation<UpdateResourceCommand> {
    @NotNull
    private Long id;
    @NotBlank
    private String resourceName;
    @NotBlank
    private String description;
    @NotBlank
    private String resourceType;
    @NotBlank
    private String httpMethod;
    @NotNull
    private int orderNum;
    @NotNull
    private Set<String> roles;

    public UpdateResourceCommand(Long id, String resourceName, String description, String resourceType, String httpMethod, int orderNum, Set<String> roles) {
        this.id = id;
        this.resourceName = resourceName;
        this.description = description;
        this.resourceType = resourceType;
        this.httpMethod = httpMethod;
        this.orderNum = orderNum;
        this.roles = roles;
        this.validateSelf();
    }
}
