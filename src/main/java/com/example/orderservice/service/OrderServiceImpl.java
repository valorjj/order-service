package com.example.orderservice.service;

import com.example.orderservice.entity.Order;
import com.example.orderservice.exception.CustomException;
import com.example.orderservice.external.client.PaymentService;
import com.example.orderservice.external.client.ProductService;
import com.example.orderservice.external.model.PaymentRequest;
import com.example.orderservice.external.model.PaymentResponse;
import com.example.orderservice.external.model.ProductResponse;
import com.example.orderservice.model.OrderRequest;
import com.example.orderservice.model.OrderResponse;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static com.example.orderservice.model.OrderStatus.PLACED;
import static com.example.orderservice.model.OrderStatus.REJECTED;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public Long placeOrder(OrderRequest orderRequest) {
        log.info("[i] product-service-svc 를 호출하여 제품의 수량을 감소시킵니다.");
        productService.reduceQuantity(orderRequest.productId(), orderRequest.quantity());

        // record 를 entity 로 변환합니다.
        Order order = OrderRequest.fromRecordToEntity(orderRequest);
        // entity 를 1차 캐시에 저장합니다.
        Order orderPS = orderRepository.save(order);

        PaymentRequest paymentRequest = PaymentRequest.builder()
            .orderId(orderPS.getId())
            .amount(orderRequest.totalAmount())
            .paymentMode(orderRequest.paymentMode())
            .build();

        OrderStatus orderStatus = null;

        try {
            log.info("[i] 결제를 진행하기 위해 payment-service-svc 를 호출합니다.");
            paymentService.doPayment(paymentRequest);
            orderStatus = PLACED;
        } catch (Exception e) {
            log.error("결제 과정중 문제가 발생했습니다.");
            orderStatus = REJECTED;
        }

        orderPS.updateOrderStatus(orderStatus);
        log.info("[i] 주문번호 [{}] 에 대한 처리가 완료되었습니다.", orderPS.getId());

        return orderPS.getId();
    }

    @Override
    @Transactional
    public OrderResponse getOrderDetails(Long orderId) {
        Order orderPS = orderRepository.findById(orderId).orElseThrow(() -> new CustomException("주문번호 [" + orderId + "] 에 대한 주문내역을 찾지 못했습니다.", HttpStatus.NOT_FOUND.value(), "Error"));

        log.info("[i] product-service-svc 를 호출합니다.");
        ProductResponse productResponse = productService.getProductById(orderPS.getProductId()).getBody();

        assert productResponse != null;
        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails
            .builder()
            .productName(productResponse.productName())
            .productId(productResponse.productId())
            .price(productResponse.price())
            .quantity(productResponse.quantity())
            .build();


        log.info("[i] payment-service 를 호출합니다.");
        PaymentResponse paymentResponse = paymentService.getPaymentDetailsByOrderId(orderPS.getId()).getBody();

        assert paymentResponse != null;
        OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails
            .builder()
            .paymentId(paymentResponse.paymentId())
            .paymentStatus(paymentResponse.paymentStatus())
            .paymentDate(paymentResponse.paymentDate())
            .paymentMode(paymentResponse.paymentMode())
            .build();

        return OrderResponse.builder()
            .orderId(orderPS.getId())
            .orderStatus(orderPS.getOrderStatus().name())
            .amount(orderPS.getAmount())
            .orderDate(orderPS.getCreatedDate())
            .paymentDetails(paymentDetails)
            .productDetails(productDetails)
            .build();
    }
}
