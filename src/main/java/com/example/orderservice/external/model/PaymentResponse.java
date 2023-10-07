package com.example.orderservice.external.model;

import com.example.orderservice.model.PaymentMode;
import com.example.orderservice.model.PaymentStatus;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;

@Builder
@JsonNaming(SnakeCaseStrategy.class)
public record PaymentResponse(
    Long paymentId,
    PaymentStatus paymentStatus,
    PaymentMode paymentMode,
    Long amount,
    LocalDateTime paymentDate,
    Long orderId
) {
}
