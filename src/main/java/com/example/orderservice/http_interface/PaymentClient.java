package com.example.orderservice.http_interface;

import com.example.orderservice.external.model.PaymentRequest;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;


/*
* 1. CircuitBreaker 적용은 어떻게 하는가
* */

@HttpExchange("/payment")
public interface PaymentClient {

    @PostExchange
    Response<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

}
