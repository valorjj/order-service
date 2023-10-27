package com.example.orderservice.controller;

import com.example.orderservice.model.OrderRequest;
import com.example.orderservice.model.OrderResponse;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    // @Qualifier(value = "orderServiceImpl")
    private final OrderService orderService;

    @PreAuthorize("hasAuthority('Customer')")
    @PostMapping("/placeOrder")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest) {
        log.info("[i] 주문 컨트롤러 접근");
        Long orderId = orderService.placeOrder(orderRequest);
        log.info("주문이 완료 되었습니다. 주문번호는 [{}] 입니다.", orderId);

        return ResponseEntity.ok(orderId);
    }


    /**
     * 특정 주문번호를 통해 해당 주문내역의 상세정보를 조회하는 url 입니다.
     *
     * @param orderId
     * @return
     */
    @PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable("id") Long orderId) {
        OrderResponse orderResponse = orderService.getOrderDetails(orderId);
        log.info("주문번호 [{}] 에 대한 세부내역을 조회 했습니다. ", orderId);

        return ResponseEntity.ok(orderResponse);
    }


}
