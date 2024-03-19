package org.opennuri.study.security.core.application.security.metadatasource;

import lombok.Setter;
import org.modelmapper.internal.util.Assert;
import org.opennuri.study.security.core.application.security.srevice.SecurityResourceService;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class UrlFilterInvocationSecurityMetadatasource implements FilterInvocationSecurityMetadataSource {

    //자원정보 권한정보 매핑 정보 저장

    private static LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap;

    public UrlFilterInvocationSecurityMetadatasource(LinkedHashMap<RequestMatcher, List<ConfigAttribute>> metadatasource) {
        requestMap = metadatasource;

    }

    //FilterInvocation, MethodInvocation 둘다 들어올수 있어 Object로 받음
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        HttpServletRequest request = ((FilterInvocation) object).getRequest();

        if (requestMap != null) {
            for (Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()) {
                RequestMatcher matcher = entry.getKey();
                if(matcher.matches(request)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        requestMap.values().forEach(allAttributes::addAll);
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    public static void setRequestMap(LinkedHashMap<RequestMatcher, List<ConfigAttribute>> reloadedMap) {
        Assert.notNull(reloadedMap, "reloadedMap must not be null");
        requestMap = reloadedMap;
    }
}
