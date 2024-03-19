package org.opennuri.study.security.core.application.port.in;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.opennuri.study.security.core.common.SelfValidation;
import org.opennuri.study.security.core.domain.Role;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
public class UpdateAccountCommand extends SelfValidation<UpdateAccountCommand> {

    @NotNull
    private Long id;
    @NotNull
    private Set<String> roles;

    public UpdateAccountCommand(Long id, Set<String> roles) {
        this.id = id;
        this.roles = roles;
        this.validateSelf();
    }
}
