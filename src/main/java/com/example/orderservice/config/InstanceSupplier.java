//package com.example.orderservice.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.cloud.client.DefaultServiceInstance;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.loadbalancer.Request;
//import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
//import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
//import reactor.core.publisher.Flux;
//
//import java.util.Arrays;
//import java.util.List;
//
//@RequiredArgsConstructor
//public class InstanceSupplier implements ServiceInstanceListSupplier {
//
//    private final String serviceId;
//
//    @Override
//    public String getServiceId() {
//        return serviceId;
//    }
//
//    @Override
//    public Flux<List<ServiceInstance>> get() {
//        return Flux.just(
//            Arrays.asList(
//                new DefaultServiceInstance("", "", "", 8080, false),
//                new DefaultServiceInstance("", "", "", 8081, false)
//            ));
//    }
//}
