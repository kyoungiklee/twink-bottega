package org.opennuri.study.security.core.application.port.in;

import lombok.*;
import org.opennuri.study.security.core.common.SelfValidation;

import javax.validation.constraints.NotBlank;
import java.util.Objects;


@Getter
@Setter
@Builder
@NoArgsConstructor
public class RoleManageCommand extends SelfValidation<RoleManageCommand> {
    private Long id;
    @NotBlank
    private String roleName;
    private String description;

    public RoleManageCommand(Long id, String roleName, String description) {
        this.id = id;
        this.roleName = roleName;
        this.description = description;
        this.validateSelf();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleManageCommand that = (RoleManageCommand) o;
        return Objects.equals(id, that.id) && Objects.equals(roleName, that.roleName) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleName, description);
    }
}
