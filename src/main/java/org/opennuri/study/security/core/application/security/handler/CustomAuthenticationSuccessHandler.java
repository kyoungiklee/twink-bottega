package org.opennuri.study.security.core.application.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    /**
     * 성공한 이후 처리해야할 적업들을 여기서 처리할 수 있다.
     * 로그인 로그, 세션 처리, 접속통계갱신 등등
     * @param request the request which caused the successful authentication
     * @param response the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     * the authentication process.
     * @throws IOException 예외
     * @throws ServletException 예외
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
            , Authentication authentication) throws IOException, ServletException {
        setDefaultTargetUrl("/"); //TODO: 이부분을 설정파일로 빼는게 좋을듯
        Optional<SavedRequest> savedRequest = Optional.ofNullable(requestCache.getRequest(request, response));
        savedRequest.ifPresentOrElse(
                cashedRequest -> useSessionUrl(cashedRequest, request, response)
                , () -> useDefaultUrl(request, response)
        );
    }

    private void useSessionUrl(SavedRequest savedRequest, HttpServletRequest request,  HttpServletResponse response)  {
        String redirectUrl = savedRequest.getRedirectUrl();
        try {
            redirectStrategy.sendRedirect(request, response, redirectUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void useDefaultUrl(HttpServletRequest request, HttpServletResponse response) {
        try {
            redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
