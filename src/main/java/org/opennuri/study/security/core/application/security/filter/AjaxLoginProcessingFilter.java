package org.opennuri.study.security.core.application.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opennuri.study.security.core.adapter.in.web.front.login.LoginRequest;
import org.opennuri.study.security.core.application.security.token.AjaxAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Optional;

import static org.opennuri.study.security.core.util.WebUtil.isAjax;

public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    public AjaxLoginProcessingFilter() {
        super(new AntPathRequestMatcher("/api/login"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if (!isAjax(request)) {
            throw new IllegalStateException("인증요청은 반드시 Ajax로 전달되어야 합니다.");
        }

        // 1. request body에서 username, password를 추출한다.
        LoginRequest loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);

        // 2. 추출한 username, password를 null 또는 빈값인지 확인한다. null 또는 빈값이면 InvalidRequestException을 발생시킨다.
        String username = Optional.ofNullable(loginRequest.getUsername()).filter(s -> !s.isEmpty())
                .orElseThrow(() -> new InvalidParameterException("username 또는 password가 누락되었습니다."));

        String password = Optional.ofNullable(loginRequest.getPassword()).filter(s -> !s.isEmpty())
                .orElseThrow(() -> new InvalidParameterException("username 또는 password가 누락되었습니다."));

        // 3. 추출한 username, password를 이용하여 AjaxAuthenticationToken을 생성한다.
        AjaxAuthenticationToken ajaxAuthenticationToken = new AjaxAuthenticationToken(username, password);

        // 4. 생성한 AjaxAuthenticationToken을 AuthenticationManager에게 전달하여 인증을 요청한다.
        return getAuthenticationManager().authenticate(ajaxAuthenticationToken);
    }
}
