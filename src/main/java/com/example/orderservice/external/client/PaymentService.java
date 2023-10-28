package com.example.orderservice.external.client;


import com.example.orderservice.exception.CustomException;
import com.example.orderservice.external.model.PaymentRequest;
import com.example.orderservice.external.model.PaymentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CircuitBreaker(name = "external", fallbackMethod = "fallback")
@FeignClient(name = "payment", url = "http://payment-service-svc/payment")
public interface PaymentService {

    @PostMapping
    ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

    @GetMapping("/order/{id}")
    ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(
        @PathVariable("id") Long orderId
    );

    default ResponseEntity<Long> fallback(Exception e) {
        throw new CustomException("payment-service-svc 가 응답하지 않습니다.", 500, e.getLocalizedMessage());
    }

}
