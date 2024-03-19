package org.opennuri.study.security.core.application.port.in;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.opennuri.study.security.core.common.SelfValidation;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class RegisterRoleHierarchyCommand extends SelfValidation<RegisterRoleHierarchyCommand> {

    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "parentName is required")
    private String parentName;

    private RegisterRoleHierarchyCommand(String name, String parentName) {

        this.name = name;
        this.parentName = parentName;
        this.validateSelf();
    }


}
