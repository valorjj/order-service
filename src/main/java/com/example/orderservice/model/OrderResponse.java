package com.example.orderservice.model;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;

@Builder
@JsonNaming(SnakeCaseStrategy.class)
public record OrderResponse(
    Long orderId,
    LocalDateTime orderDate,
    String orderStatus,
    Long amount,
    ProductDetails productDetails,
    PaymentDetails paymentDetails
) {

    @Builder
    @JsonNaming(SnakeCaseStrategy.class)
    public record ProductDetails(
        String productName,
        Long productId,
        Long quantity,
        Long price

    ) {

    }


    @Builder
    @JsonNaming(SnakeCaseStrategy.class)
    public record PaymentDetails(
        Long paymentId,
        PaymentMode paymentMode,
        PaymentStatus paymentStatus,
        LocalDateTime paymentDate
    ) {

    }


}
