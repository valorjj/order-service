package com.example.orderservice.external.model;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;

@Builder
@JsonNaming(SnakeCaseStrategy.class)
public record ProductRequest(

    String productName,
    Long price,
    Long quantity

) {
}
