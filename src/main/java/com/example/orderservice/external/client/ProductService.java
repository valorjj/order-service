package com.example.orderservice.external.client;


import com.example.orderservice.config.FeignConfig;
import com.example.orderservice.exception.CustomException;
import com.example.orderservice.external.model.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CircuitBreaker(name = "external", fallbackMethod = "fallback")
@FeignClient(name = "product", url = "http://product-service-svc/product")
public interface ProductService {

    @PutMapping(value = "/reduceQuantity/{id}")
    ResponseEntity<Integer> reduceQuantity(
        @PathVariable("id") Long productId,
        @RequestParam Long quantity
    );

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProductResponse> getProductById(
        @PathVariable("id") Long productId
    );

    default ResponseEntity<Void> fallback(Exception e) {
        throw new CustomException("product-service-svc 가 응답하지 않습니다.", 500, e.getMessage());
    }

}
