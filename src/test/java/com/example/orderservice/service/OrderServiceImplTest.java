package com.example.orderservice.service;

import com.example.orderservice.TestAnnotation;
import com.example.orderservice.entity.Order;
import com.example.orderservice.exception.CustomException;
import com.example.orderservice.external.client.PaymentService;
import com.example.orderservice.external.client.ProductService;
import com.example.orderservice.external.model.PaymentRequest;
import com.example.orderservice.external.model.PaymentResponse;
import com.example.orderservice.external.model.ProductResponse;
import com.example.orderservice.model.OrderRequest;
import com.example.orderservice.model.OrderResponse;
import com.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.example.orderservice.model.OrderStatus.PLACED;
import static com.example.orderservice.model.PaymentMode.CASH;
import static com.example.orderservice.model.PaymentStatus.SUCCESS;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceImplTest {


    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private OrderServiceImpl orderService;

    @Value("${microservices.product}")
    private String productServiceUrl;

    @Value("${microservices.payment}")
    private String paymentServiceUrl;


    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, productService, paymentService, restTemplate);

        // 테스트 케이스를 실행하는 시점에 url 값이 들어가야하므로 reflection 을 사용
        ReflectionTestUtils.setField(orderService, "productServiceUrl", productServiceUrl);
        ReflectionTestUtils.setField(orderService, "paymentServiceUrl", paymentServiceUrl);
    }

    @TestAnnotation
    @DisplayName("Get Order - Success")
    void test_When_Get_Order_Success() {
        // Mocking
        Order mockOrder = getMockOrder();

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));

        when(restTemplate.getForObject(productServiceUrl + mockOrder.getProductId(), ProductResponse.class))
            .thenReturn(getMockProductResponse());

        when(restTemplate.getForObject(paymentServiceUrl + "order/" + mockOrder.getId(), PaymentResponse.class))
            .thenReturn(getMockPaymentResponse());

        // Acutal
        OrderResponse actualOrderResponse = orderService.getOrderDetails(1L);

        // Verification
        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).getForObject(
            productServiceUrl + mockOrder.getProductId(), ProductResponse.class
        );
        verify(restTemplate, times(1)).getForObject(
            paymentServiceUrl + "order/" + mockOrder.getId(), PaymentResponse.class);

        Assertions.assertNotNull(actualOrderResponse);
        Assertions.assertEquals(mockOrder.getId(), actualOrderResponse.orderId());

    }

    @TestAnnotation
    @DisplayName("Get Order - Failure")
    void test_When_Get_Order_Failure() {
        when(orderRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        CustomException exception = Assertions.assertThrows(CustomException.class,
            () -> orderService.getOrderDetails(1L)
        );

        Assertions.assertEquals(404, exception.getStatus());

        verify(orderRepository, times(1)).findById(anyLong());
    }


    @TestAnnotation
    @DisplayName("Place Order - Success")
    void test_When_Place_Order_Success() {
        // 1. Mocking
        Order mockOrder = getMockOrder();
        OrderRequest mockOrderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(productService.reduceQuantity(anyLong(), anyLong()))
            .thenReturn(new ResponseEntity<>(1, HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
            .thenReturn(new ResponseEntity<>(1L, HttpStatus.OK));

        // Actual
        Long actualOrderId = orderService.placeOrder(mockOrderRequest);

        // Verify
        verify(orderRepository, times(1)).save(any());
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));

        // Assert
        Assertions.assertEquals(mockOrder.getId(), actualOrderId);

    }

    @DisplayName("Place Order - Payment Failure")
    @TestAnnotation
    void test_When_Place_Order_Payment_Fails_Then_Order_Placed() {
        // Mocking
        Order mockOrder = getMockOrder();
        OrderRequest mockOrderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(productService.reduceQuantity(anyLong(), anyLong()))
            .thenReturn(ResponseEntity.ok(1));
        when(paymentService.doPayment(any(PaymentRequest.class)))
            .thenThrow(new RuntimeException());

        Long actualOrderId = orderService.placeOrder(mockOrderRequest);

        verify(orderRepository, times(1)).save(any());
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));

        Assertions.assertEquals(mockOrder.getId(), actualOrderId);
    }


    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
            .productId(1L)
            .quantity(9L)
            .paymentMode(CASH)
            .totalAmount(10000L)
            .build();
    }


    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
            .paymentId(1L)
            .paymentMode(CASH)
            .amount(20000L)
            .orderId(1L)
            .paymentStatus(SUCCESS)
            .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
            .productId(1L)
            .productName("test-product")
            .price(10000L)
            .quantity(9999L)
            .build();
    }

    private Order getMockOrder() {
        return Order.builder()
            .id(1L)
            .orderStatus(PLACED)
            .amount(20000L)
            .quantity(2L)
            .productId(1L)
            .build();
    }

}