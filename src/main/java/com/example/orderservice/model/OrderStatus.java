package com.example.orderservice.model;

import lombok.Getter;

@Getter
public enum OrderStatus {

    PLACED,
    CANCELLED,
    REJECTED,
    FAILED,
    PENDING;

}
