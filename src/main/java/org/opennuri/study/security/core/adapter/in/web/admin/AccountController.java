package org.opennuri.study.security.core.adapter.in.web.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.opennuri.study.security.core.application.port.in.AccountManageUseCase;
import org.opennuri.study.security.core.application.port.in.RoleManageUseCase;
import org.opennuri.study.security.core.application.port.in.UpdateAccountCommand;
import org.opennuri.study.security.core.domain.Account;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@Slf4j
public class AccountController {
    private final AccountManageUseCase accountManageUseCase;
    private final RoleManageUseCase roleManageUseCase;

    @GetMapping("/admin/accounts")
    public String getAccounts(Model model) {

        Optional<List<Account>> all = accountManageUseCase.findAll();

        List<Account> accounts = all.orElse(new ArrayList<>());
        model.addAttribute("accounts", accounts);
        return "admin/account/list";
    }

    //http://localhost:8080/admin/admin/accounts/4
    @GetMapping("/admin/accounts/{id}")
    public String getAccount(@PathVariable("id") Long id, Model model) {

        Optional<Account> optionalAccount = accountManageUseCase.findById(id);
        log.info("optionalAccount: {}", optionalAccount);

        Account account = optionalAccount.orElse(Account.from(
                new Account.Id(null)
                , new Account.Username(null)
                , new Account.Password(null)
                , new Account.Email(null)
                , new Account.Roles(Set.of(Role.from(
                        new Role.Id(null)
                        , new Role.RoleName(null)
                        , new Role.Description(null)
                )))
        ));

        List<String> userRoleNameList = account.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

        Optional<List<Role>> optionalRoles = roleManageUseCase.findAll();
        List<Role> roles = optionalRoles.orElse(List.of(Role.from(
                new Role.Id(null)
                , new Role.RoleName(null)
                , new Role.Description(null)
        )));

        model.addAttribute("account", account);
        model.addAttribute("roleList", roles);
        model.addAttribute("userRoleNames", userRoleNameList);

        return "admin/account/detail";
    }

    @PostMapping("/admin/accounts")
    public String updateAccount(UpdateAccountRequest updateAccountRequest, HttpServletRequest request) {
        request.getAttributeNames().asIterator().forEachRemaining(name -> {
            log.info("name: {}", name);
        });

        request.getParameterNames().asIterator().forEachRemaining(name -> {
            log.info(name + ": " + request.getParameter(name));
        });
        log.info("updateAccountRequest: {}", updateAccountRequest);

        UpdateAccountCommand updateAccountCommand = UpdateAccountCommand.builder()
                .id(updateAccountRequest.getId())
                .roles(updateAccountRequest.getRoles())
                .build();
        Account account = accountManageUseCase.updateAccount(updateAccountCommand);
        log.info("find account : {}", account);

        return "redirect:/admin/accounts";
    }

    @GetMapping("/admin/accounts/delete/{id}")
    public String deleteAccount(@PathVariable("id") Long id) {

        boolean isSuccess = accountManageUseCase.deleteAccount(id);

        return "redirect: /admin/accounts";
    }
}
