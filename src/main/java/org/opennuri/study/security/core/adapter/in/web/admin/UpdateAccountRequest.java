package org.opennuri.study.security.core.adapter.in.web.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UpdateAccountRequest {
    @NotNull(message = "아이디는 필수입니다.")
    private Long id;
    @NotNull(message = "권한을 한개 이상 선택해주세요")
    private Set<String> roles;

}
