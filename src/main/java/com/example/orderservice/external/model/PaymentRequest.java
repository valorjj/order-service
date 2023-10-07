package com.example.orderservice.external.model;

import com.example.orderservice.model.PaymentMode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@Builder
@JsonNaming(SnakeCaseStrategy.class)
public record PaymentRequest(
    Long orderId,
    Long amount,
    String referenceNumber,
    PaymentMode paymentMode

) {

}
