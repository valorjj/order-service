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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static com.example.orderservice.model.OrderStatus.PLACED;
import static com.example.orderservice.model.OrderStatus.REJECTED;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final RestTemplate restTemplate;

    @Value(value = "${microservices.product}")
    private String productServiceUrl;

    @Value(value = "${microservices.payment}")
    private String paymentServiceUrl;

    public OrderServiceImpl(OrderRepository orderRepository, ProductService productService, PaymentService paymentService, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.paymentService = paymentService;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    public Long placeOrder(OrderRequest orderRequest) {
        log.info("[i] 주문내역 저장을 시작합니다.");
        log.info("[i] 제품 재고를 업데이트하기 위해서 product-service 를 호출합니다.");
        productService.reduceQuantity(orderRequest.productId(), orderRequest.quantity());

        // record 를 entity 로 변환합니다.
        Order order = OrderRequest.of(orderRequest);
        // entity 를 1차 캐시에 저장합니다.
        Order orderPS = orderRepository.save(order);
        log.info("[i] 주문내역이 성공적으로 저장되었습니다.");

        PaymentRequest paymentRequest = PaymentRequest.builder()
            .orderId(orderPS.getId())
            .amount(orderRequest.totalAmount())
            .paymentMode(orderRequest.paymentMode())
            .build();

        OrderStatus orderStatus = null;

        try {
            log.info("[i] 결제를 진행하기 위해 Payment-Service 를 호출합니다.");
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
        log.info("[i] 주문번호 [{}] 에 대한 상세내역을 조회합니다.", orderId);
        Order orderPS = orderRepository.findById(orderId).orElseThrow(() -> new CustomException("주문번호 [" + orderId + "] 에 대한 주문내역을 찾지 못했습니다.", HttpStatus.NOT_FOUND.value()));

        log.info("restTemplate 문제인지 확인하기 위해서 FeignClient 으로 등록된 메서드를 직접 호출해보겠습니다.");


        log.info("[i] 주문번호 [{}] 에 해당하는 제품(제품번호 [{}]) 조회하기 위해서 product-service 를 호출합니다.", orderId, orderPS.getProductId());
//        ProductResponse productResponse
//            = restTemplate.getForObject(productServiceUrl + orderPS.getProductId(),
//            ProductResponse.class
//        );

        ProductResponse productResponse = productService.getProductById(orderPS.getProductId()).getBody();

        log.info("[i] ProductResponse 는 다음과 같습니다 [{}]", productResponse);

        log.info("[i] 주문번호 [{}] 에 해당하는 결제내역을 조회하기 위해서 payment-service 를 호출합니다.", orderId);
        PaymentResponse paymentResponse
            = restTemplate.getForObject(paymentServiceUrl + "order/" + orderPS.getId(),
            PaymentResponse.class
        );
        log.info("[i] PaymentResponse 는 다음과 같습니다. [{}]", paymentResponse);

        assert productResponse != null;
        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails
            .builder()
            .productName(productResponse.productName())
            .productId(productResponse.productId())
            .price(productResponse.price())
            .quantity(productResponse.quantity())
            .build();
        log.info("[i] OrderResponse.ProductDetails 는 다음과 같습니다. := [{}]", productDetails);

        assert paymentResponse != null;
        OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails
            .builder()
            .paymentId(paymentResponse.paymentId())
            .paymentStatus(paymentResponse.paymentStatus())
            .paymentDate(paymentResponse.paymentDate())
            .paymentMode(paymentResponse.paymentMode())
            .build();

        log.info("[i] OrderResponse.PaymentDetails 는 다음과 같습니다. := [{}]", paymentDetails);

        OrderResponse orderResponse = OrderResponse.builder()
            .orderId(orderPS.getId())
            .orderStatus(orderPS.getOrderStatus().name())
            .amount(orderPS.getAmount())
            .orderDate(orderPS.getCreatedDate())
            .paymentDetails(paymentDetails)
            .productDetails(productDetails)
            .build();
        log.info("[i] OrderResponse 는 다음과 같습니다. := [{}]", orderResponse);

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
