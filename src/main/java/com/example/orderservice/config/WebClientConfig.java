//package com.example.orderservice.config;
//
//import jakarta.validation.constraints.Null;
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
//import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
//import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
//
//@Configuration
//@LoadBalancerClient(name = "order-service", configuration = Null.class)
//public class WebClientConfig {
//
//    @Bean
//    public OAuth2AuthorizedClientManager clientManager(
//        ClientRegistrationRepository clientRegistrationRepository,
//        OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository
//    ) {
//        OAuth2AuthorizedClientProvider oAuth2AuthorizedClientProvider
//            = OAuth2AuthorizedClientProviderBuilder
//            .builder()
//            .clientCredentials()
//            .build();
//
//        DefaultOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager
//            = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository,
//            oAuth2AuthorizedClientRepository);
//
//        oAuth2AuthorizedClientManager.setAuthorizedClientProvider(oAuth2AuthorizedClientProvider);
//
//        return oAuth2AuthorizedClientManager;
//    }
//}
