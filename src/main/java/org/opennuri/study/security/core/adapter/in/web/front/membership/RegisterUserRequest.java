package org.opennuri.study.security.core.adapter.in.web.front.membership;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RegisterUserRequest {
    private String name;
    private String password;
    private String email;

    @Override
    public String toString() {
        return "RegisterUserRequest{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
