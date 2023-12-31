package com.example.orderservice;

import com.example.orderservice.entity.Order;
import com.example.orderservice.external.PaymentService;
import com.example.orderservice.external.ProductService;
import com.example.orderservice.external.request.PaymentMode;
import com.example.orderservice.external.response.PaymentResponse;
import com.example.orderservice.external.response.ProductResponse;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.request.OrderRequest;
import com.example.orderservice.response.OrderResponse;
import com.example.orderservice.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ProductService productService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderService orderService;

    @DisplayName("Test order service when success")
    @Test
    void test_When_Order_Success(){

        // Mock
        Order order = getMockOrder();

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        when(restTemplate.getForObject("http://PRODUCT-SERVICE/api/product/get/" + order.getProductId(), ProductResponse.class))
                .thenReturn(getMockProductResponse());

        when(restTemplate.getForObject("http://PAYMENT-SERVICE/api/payment/get/" + order.getProductId(), PaymentResponse.class))
                .thenReturn(getMockPaymentResponse());

        // Actual
        OrderResponse orderResponse = orderService.findById(1);

        //Verefication
        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).getForObject(
                "http://PRODUCT-SERVICE/api/product/get/" + order.getProductId(),
                ProductResponse.class);
        verify(restTemplate, times(1)).getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class);

        //Assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(), orderResponse.getOrderId());

    }



    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1)
                .paymentDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
                .amount(200)
                .orderId(1)
                .status("ACCEPTED")
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .id(String.valueOf(1))
                .name("iPhone")
                .price("100")
                .quantity("200")
                .build();
    }

    private Order getMockOrder() {
        return Order.builder()
                .orderStatus("PLACED")
                .orderTime(Instant.now())
                .id(1)
                .amount(100)
                .quantity(200)
                .productId(1)
                .build();
    }

}
