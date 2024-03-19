package org.opennuri.study.security.core.application.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opennuri.study.security.core.domain.Account;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
            , Authentication authentication) throws IOException{

        Account account = (Account)authentication.getPrincipal();

        Map<String, String> success = new HashMap<>();
        success.put("success", "true");
        success.put("role", account.getRoles().toString());
        success.put("username", account.getEmail());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), success);
    }
}
