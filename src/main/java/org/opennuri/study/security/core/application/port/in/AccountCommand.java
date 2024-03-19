package org.opennuri.study.security.core.application.port.in;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.opennuri.study.security.core.common.SelfValidation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Builder
@Setter @Getter
public class AccountCommand extends SelfValidation<AccountCommand> {

    private Long id;

    @NotEmpty
    private String name;
    @NotEmpty
    private String password;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
            , message = "올바른 형식의 이메일 주소여야 합니다")
    private String email;

    public AccountCommand(Long id, String name, String password, String email) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.validateSelf();
    }
}
