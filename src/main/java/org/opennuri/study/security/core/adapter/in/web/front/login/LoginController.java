package org.opennuri.study.security.core.adapter.in.web.front.login;

import org.opennuri.study.security.core.domain.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {
    @GetMapping(value = {"/login", "/api/login"})
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "exception", required = false) String exception,
            Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (exception != null) {
            model.addAttribute("exception", exception);
        }
        return "front/login/loginForm";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/";
    }

    @GetMapping(value = {"/denied", "/api/denied"})
    public String denied(
            @RequestParam(value = "exception", required = false) String exception, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Account principal = (Account) authentication.getPrincipal();
            model.addAttribute("username", principal.getUsername());
        }
        model.addAttribute("exception", exception);
        return "front/login/denied";
    }
}
