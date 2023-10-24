package com.example.orderservice.config;

import com.example.orderservice.external.decoder.CustomErrorDecoder;
import com.example.orderservice.external.interceptor.RestTemplateInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestTemplate;


import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableFeignClients(basePackages = "com.example.orderservice")
public class FeignConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    @Bean
    ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of(new RestTemplateInterceptor(clientManager(clientRegistrationRepository, oAuth2AuthorizedClientRepository))));
        return restTemplate;
    }


    @Bean
    public OAuth2AuthorizedClientManager clientManager(
        ClientRegistrationRepository clientRegistrationRepository,
        OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository
    ) {
        OAuth2AuthorizedClientProvider oAuth2AuthorizedClientProvider
            = OAuth2AuthorizedClientProviderBuilder
            .builder()
            .clientCredentials()
            .build();

        DefaultOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager
            = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository,
            oAuth2AuthorizedClientRepository);

        oAuth2AuthorizedClientManager.setAuthorizedClientProvider(oAuth2AuthorizedClientProvider);

        return oAuth2AuthorizedClientManager;
    }

}
