package org.opennuri.study.security.core.application.port.in;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.opennuri.study.security.core.common.SelfValidation;
import org.opennuri.study.security.core.domain.Resource;
import org.opennuri.study.security.core.domain.Role;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@Builder
public class RegisterResourceCommand extends SelfValidation<RegisterResourceCommand> {
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

    public RegisterResourceCommand(String resourceName, String description, String resourceType, String httpMethod, int orderNum, Set<String> roles) {
        this.resourceName = resourceName;
        this.description = description;
        this.resourceType = resourceType;
        this.httpMethod = httpMethod;
        this.orderNum = orderNum;
        this.roles = roles;
        this.validateSelf();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterResourceCommand command = (RegisterResourceCommand) o;
        return orderNum == command.orderNum && Objects.equals(resourceName, command.resourceName) && Objects.equals(description, command.description) && Objects.equals(resourceType, command.resourceType) && Objects.equals(httpMethod, command.httpMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceName, description, resourceType, httpMethod, orderNum);
    }
}
