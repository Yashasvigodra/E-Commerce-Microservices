package com.mini.order_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// order-service: config/FeignAuthInterceptor.java
@Component
@RequiredArgsConstructor
public class FeignAuthInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final ServiceTokenProvider serviceTokenProvider; // new helper, shown below

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String authHeader = null;
        if (attributes != null) {
            authHeader = attributes.getRequest().getHeader(AUTHORIZATION_HEADER);
        }

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            template.header(AUTHORIZATION_HEADER, authHeader); // user-initiated path
        } else {
            // system-initiated path (webhook, scheduled job) — use service-account token
            String serviceToken = serviceTokenProvider.getToken();
            template.header(AUTHORIZATION_HEADER, BEARER_PREFIX + serviceToken);
        }
    }
}