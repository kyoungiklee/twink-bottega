package org.opennuri.study.security.core.adapter.out.persistence.account.history;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "ACCOUNT_HISTORY")
@Builder
@AllArgsConstructor
public class AccountHistoryEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "ACCOUNT_ID")
    private Long accountId;
    @Column(name = "NAME")
    private String username;
    private String email;
    private String password;
    private String roles;
    private LocalDateTime createdDateTime;
    private String createdUser;
    private LocalDateTime modifiedDateTime;
    private String modifiedUser;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;

}
