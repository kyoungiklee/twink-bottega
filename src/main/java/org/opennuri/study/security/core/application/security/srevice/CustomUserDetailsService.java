package org.opennuri.study.security.core.application.security.srevice;

import org.opennuri.study.security.core.application.port.in.AccountManageUseCase;
import org.opennuri.study.security.core.domain.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AccountManageUseCase accountManageUseCase;

    public CustomUserDetailsService(AccountManageUseCase accountManageUseCase) {
        Assert.notNull(accountManageUseCase, "Cannot pass a null accountManageUseCase");
        this.accountManageUseCase = accountManageUseCase;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountManageUseCase.findByEmail(email);
        Account account = optionalAccount
                .orElseThrow(() -> new UsernameNotFoundException("이메일 또는 비밀번호가 일치하지 않습니다. 다시 입력바랍니다."));

        return new AccountContext(account, getAuthorities(account));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Account account) {
        return account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toSet());
    }
}
