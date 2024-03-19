package org.opennuri.study.security.core.application.security.handler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Getter @Setter
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private String errorPage = "/denied";
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response
            , AccessDeniedException accessDeniedException) throws IOException, ServletException {
        //String deniedUrl = errorPage + "?exception=" + accessDeniedException.getMessage();
        String deniedUrl = errorPage + "?exception=Access Denied";
        response.sendRedirect(deniedUrl);
    }
}
