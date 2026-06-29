package com.mini.order_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


// order-service: config/ServiceTokenProvider.java
@Component
public class ServiceTokenProvider {

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private String cachedToken;
    private Instant tokenExpiry = Instant.MIN;
    private final Object lock = new Object(); // fixes Bug 3 from before

    public String getToken() {
        synchronized (lock) {
            if (Instant.now().isAfter(tokenExpiry)) {
                cachedToken = fetchServiceToken();
                tokenExpiry = Instant.now().plusSeconds(270);
            }
            return cachedToken;
        }
    }

    private String fetchServiceToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        Map<String, Object> response = restTemplate.postForObject(tokenUri, request, Map.class);
        return (String) response.get("access_token");
    }
}
