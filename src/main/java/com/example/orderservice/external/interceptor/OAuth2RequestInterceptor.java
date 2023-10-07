package com.example.orderservice.external.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import java.util.Objects;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class OAuth2RequestInterceptor implements RequestInterceptor {

    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", "Bearer "
            + Objects.requireNonNull(oAuth2AuthorizedClientManager.authorize(OAuth2AuthorizeRequest
            .withClientRegistrationId("internal-client")
            .principal("internal")
            .build())).getAccessToken().getTokenValue());
    }
}
