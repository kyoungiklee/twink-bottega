package org.opennuri.study.security.core.application.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

public class AjaxAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String errorMessage = "Invalid username or password";

        if (exception instanceof BadCredentialsException) {
            errorMessage = "이메일 또는 패스워드가 맞지않습니다. 다시 로그인 바랍니다.";
        } else if (exception instanceof InsufficientAuthenticationException) {
            errorMessage = "Invalid secret key";
        } else if (exception instanceof CredentialsExpiredException) {
            errorMessage = "패스워드가 만료되었습니다. 패스워드를 다시 설정 바랍니다. ";
        }

        Map<String, String> error = new HashMap<>();
        error.put("error", "true");
        error.put("message", errorMessage);
        error.put("exception", exception.getMessage());

        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), error);
    }
}
