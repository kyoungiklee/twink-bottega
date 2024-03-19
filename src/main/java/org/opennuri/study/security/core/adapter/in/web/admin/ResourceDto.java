package org.opennuri.study.security.core.adapter.in.web.admin;

import lombok.*;

import javax.validation.constraints.*;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ResourceDto {
    private Long id;
    @NotBlank(message = "리소스 이름은 필수입니다.")
    @Size(max = 50, message = "50자 이내로 입력하여야 합니다.")
    private String resourceName;

    @NotBlank(message = "리소스 타입은 필수입니다.")
    @Size(max = 10, message = "10자 이내로 입력하여야 합니다.")
    private String resourceType;

    @NotBlank(message = "HTTP Method는 필수입니다.")
    @Size(max = 10, message = "10자 이내로 입력하여야 합니다.")
    private String httpMethod;

    @NotNull(message = "순서는 필수입니다.")
    @Positive(message = "양수로 입력하여야 합니다.")
    private int orderNum;

    @NotBlank(message = "리소스 설명은 필수입니다.")
    @Size(max = 100, message = "리소스 설명은 100자 이내로 입력하여야 합니다.")
    private String description;

    @NotNull(message = "권한은 필수입니다.")
    @NotEmpty(message = "권한은 최소 1개 이상 선택하여야 합니다.")
    private Set<String> roles;

}
