package com.example.orderservice.model;

import com.example.orderservice.entity.Order;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;

@Builder
@JsonNaming(SnakeCaseStrategy.class)
public record OrderRequest(
    Long productId,
    Long totalAmount,
    Long quantity,
    PaymentMode paymentMode
) {
    public static Order fromRecordToEntity(OrderRequest orderRequest) {
        return Order.builder()
            .productId(orderRequest.productId())
            .orderStatus(OrderStatus.PLACED)
            .amount(orderRequest.totalAmount())
            .quantity(orderRequest.quantity())
            .build();
    }
}
