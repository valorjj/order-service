package com.example.orderservice.controller;

import com.example.orderservice.config.OrderServiceConfig;
import com.example.orderservice.entity.Order;
import com.example.orderservice.model.OrderRequest;
import com.example.orderservice.model.OrderResponse;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.model.PaymentMode;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = {OrderServiceConfig.class})
class OrderControllerTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
        .options(WireMockConfiguration.wireMockConfig().port(8081))
        .build();

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules()
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void setUp() throws IOException {
        getProductDetailsResponse();
        doPayment();
        getPaymentDetails();
        reduceQuantity();
    }

    private void getPaymentDetails() throws IOException {
        circuitBreakerRegistry.circuitBreaker("external").reset();
        wireMockServer.stubFor(get(urlMatching("/payment/.*"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(StreamUtils.copyToString(OrderControllerTest.class.getClassLoader()
                    .getResourceAsStream("mock/GetPayment.json"), Charset.defaultCharset()))
            )
        );

    }

    private void reduceQuantity() {
        circuitBreakerRegistry.circuitBreaker("external").reset();
        wireMockServer.stubFor(put(urlMatching("/product/reduceQuantity/.*"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            )
        );
    }

    private void doPayment() {
        wireMockServer.stubFor(post(urlEqualTo("/payment"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            )
        );
    }

    private void getProductDetailsResponse() throws IOException {
        // json 파일로 등록한 제품번호 id 중 1번 제품을 조회한다.
        wireMockServer.stubFor(get("/product/1")
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(StreamUtils.copyToString(OrderControllerTest.class.getClassLoader()
                    .getResourceAsStream("mock/GetProduct.json"), Charset.defaultCharset()))
            )
        );
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
            .productId(1L)
            .paymentMode(PaymentMode.CASH)
            .quantity(2L)
            .totalAmount(20000L)
            .build();
    }

    @Test
    @DisplayName("Place Order - Success")
    void test_When_Place_Order_DoPayment_Success() throws Exception {
        // 1.
        OrderRequest mockOrderRequest = getMockOrderRequest();

        // 2.
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/order/placeOrder")
                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("Customer")))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(mockOrderRequest))
            ).andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        // 3.
        Long expectedOrderId = Long.valueOf(mvcResult.getResponse().getContentAsString());

        // 4.
        Optional<Order> orderOP = orderRepository.findById(expectedOrderId);
        Assertions.assertTrue(orderOP.isPresent());

        // 5.
        Order order = orderOP.get();
        Assertions.assertEquals(expectedOrderId, order.getId());
        Assertions.assertEquals(OrderStatus.PLACED, order.getOrderStatus());
        Assertions.assertEquals(mockOrderRequest.totalAmount(), order.getAmount());
        Assertions.assertEquals(mockOrderRequest.quantity(), order.getQuantity());
    }

    /**
     * Customer 권한을 가진 사용자 접근이 가능한 url 에 Admin 권한을 가진 사용자로 요청을 보낸다.
     *
     * @throws Exception
     */
    @Test
    @DisplayName("Place Order - 403 Forbidden")
    void test_When_Place_Order_With_Wrong_Access_Then_Throw403() throws Exception {
        // 1.
        OrderRequest mockOrderRequest = getMockOrderRequest();

        // 2.
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/order/placeOrder")
            .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("Admin")))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(mockOrderRequest))
        ).andExpect(MockMvcResultMatchers.status().isForbidden()).andReturn();
    }

    @Test
    @DisplayName("Get Order - Success")
    void test_When_Get_Order_Success() throws Exception {

        // 1. order-service 에 접근해서 주문내역을 조회한다.
        // 주문은 통합 테스트 내 다른 메서드에서 이루어진다.
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/order/1")
                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("Admin")))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            ).andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        // 2.
        String actualResponse = mvcResult.getResponse().getContentAsString();

        // 3.
        Optional<Order> order = orderRepository.findById(1L);
        Assertions.assertTrue(order.isPresent());

        // 4.
        String expectedResponse = getOrderResponse(order.get());

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    private String getOrderResponse(Order order) throws IOException {
        OrderResponse.PaymentDetails paymentDetails
            = objectMapper.readValue(
            StreamUtils.copyToString(OrderControllerTest.class.getClassLoader()
                .getResourceAsStream("mock/GetPayment.json"), Charset.defaultCharset()
            ), OrderResponse.PaymentDetails.class);

        OrderResponse.ProductDetails productDetails
            = objectMapper.readValue(
            StreamUtils.copyToString(OrderControllerTest.class.getClassLoader()
                .getResourceAsStream("mock/GetProduct.json"), Charset.defaultCharset()
            ), OrderResponse.ProductDetails.class);

        OrderResponse orderResponse
            = OrderResponse.builder()
            .paymentDetails(paymentDetails)
            .productDetails(productDetails)
            .orderId(order.getId())
            .orderDate(order.getCreatedDate())
            .orderStatus(order.getOrderStatus().name())
            .amount(order.getAmount())
            .build();

        return objectMapper.writeValueAsString(orderResponse);
    }

}

