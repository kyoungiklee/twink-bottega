package org.opennuri.study.security.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.opennuri.study.security.core.application.port.in.UpdateAccountCommand;

import java.io.Serializable;
import java.util.Set;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
/**
 * ERROR o.a.catalina.session.StandardManager - Exception loading sessions from persistent storage 오류
 * 처리를 위해 Serializable implements함
 */
public class Account implements Serializable{
    private final Long id;
    private final String username;
    private final String password;
    private final String email;
    private final Set<Role> roles;

    public static Account from(Id id, Username username, Password password, Email email, Roles roles) {
        return new Account(id.id, username.username, password.password, email.email, roles.roles);
    }

    public static class Id {
        private final Long id;
        public Id(Long id) {
            this.id = id;
        }
    }

    public static class Username {
        private final String username;
        public Username(String username) {
            this.username = username;
        }
    }

    public static class Password {
        private final String password;
        public Password(String password) {
            this.password = password;
        }
    }

    public static class Email {
        private final String email;
        public Email(String email) {
            this.email = email;
        }
    }

    public static class Roles {
        private final Set<Role> roles;
        public Roles(Set<Role> roles) {
            this.roles = roles;
        }
    }

    public static void main(String[] args) {
        new Id(1L);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }
}
