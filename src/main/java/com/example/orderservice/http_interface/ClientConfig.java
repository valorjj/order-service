package com.example.orderservice.http_interface;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private final LoadBalancedExchangeFilterFunction filterFunction;

    @Bean
    public WebClient productWebClient() {
        return WebClient.builder()
            .baseUrl("http://product-service-svc")
            .filter(filterFunction)
            .build();

    }

    @Bean
    public ProductClient productClient() {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builder(WebClientAdapter.forClient(productWebClient()))
            .build();
        return factory.createClient(ProductClient.class);
    }

    @Bean
    public WebClient paymentWebClient() {
        return WebClient.builder()
            .baseUrl("http://payment-service-svc")
            .build();
    }


}
