package com.example.orderservice.http_interface;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {

    @Bean
    ProductClient productClient() {
        WebClient client = WebClient.builder()
            .baseUrl("http://product-service-svc")
            .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(ProductClient.class);
    }

    @Bean
    PaymentClient paymentClient() {
        WebClient client = WebClient.builder()
            .baseUrl("http://payment-service-svc")
            .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(PaymentClient.class);
    }


}
