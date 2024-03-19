package org.opennuri.study.security.core.application.port.in;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.opennuri.study.security.core.common.SelfValidation;
import org.opennuri.study.security.core.domain.Role;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
public class RegisterAccountCommand extends SelfValidation<RegisterAccountCommand> {
    @NotEmpty
    private String name;
    private String password;
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
            , message = "올바른 형식의 이메일 주소여야 합니다")
    private String email;
    private Set<Role> roles;

    public RegisterAccountCommand(String name, String password, String email, Set<Role> roles) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.validateSelf();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterAccountCommand that = (RegisterAccountCommand) o;
        return Objects.equals(name, that.name)  && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }
}
