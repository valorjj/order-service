package com.example.orderservice.entity;

import com.example.orderservice.common.BaseTime;
import com.example.orderservice.model.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Table(name = "ORDER_TBL")
@NoArgsConstructor(access = PROTECTED)
@Getter
public class Order extends BaseTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ORDER_ID")
    private Long id;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "QUANTITY")
    private Long quantity;

    @Column(name = "ORDER_STATUS")
    @Enumerated(STRING)
    private OrderStatus orderStatus;

    @Column(name = "TOTAL_AMOUNT")
    private Long amount;

    @Builder
    public Order(Long id, Long productId, Long quantity, OrderStatus orderStatus, Long amount) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.orderStatus = orderStatus;
        this.amount = amount;
    }

    @Builder
    public Order(Long productId, Long quantity, OrderStatus orderStatus, Long amount) {
        this.productId = productId;
        this.quantity = quantity;
        this.orderStatus = orderStatus;
        this.amount = amount;
    }

    public void updateOrderStatus(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }

}
