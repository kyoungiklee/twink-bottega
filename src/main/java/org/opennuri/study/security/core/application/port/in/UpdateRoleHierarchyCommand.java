package org.opennuri.study.security.core.application.port.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.opennuri.study.security.core.common.SelfValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class UpdateRoleHierarchyCommand extends SelfValidation<UpdateRoleHierarchyCommand> {

    @NotNull(message = "id is required")
    private Long id;
    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "parentName is required")
    private String parentName;

    public UpdateRoleHierarchyCommand(Long id, String name, String parentName) {

        this.id = id;
        this.name = name;
        this.parentName = parentName;
        this.validateSelf();
    }
}
