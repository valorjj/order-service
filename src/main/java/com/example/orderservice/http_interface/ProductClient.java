package com.example.orderservice.http_interface;

import com.example.orderservice.external.model.ProductResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange("/product")
public interface ProductClient {

    @PutExchange("/reduceQuantity/{id}")
    Integer reduceQuantity(@PathVariable("id") Long id, @RequestParam Long quantity);

    @GetExchange("/{id}")
    ProductResponse productById(@PathVariable("id") Long id);

}
