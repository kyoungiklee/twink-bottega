package org.opennuri.study.security.core.adapter.in.web.admin;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Slf4j
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RoleDto {

    private Long id;
    @NotBlank(message = "권한명은 필수입니다.")
    @Size(max = 20, message = "20자 이내로 입력하여야 합니다.")
    private String roleName;

    @NotBlank(message = "권한설명은 필수입니다.")
    @Size(max = 50, message = "50자 이내로 입력하여야합니다.")
    private String description;

    @Override
    public String toString() {
        return "RoleDto{" +
                "roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
