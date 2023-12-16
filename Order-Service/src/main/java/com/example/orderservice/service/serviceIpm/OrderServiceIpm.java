package com.example.orderservice.service.serviceIpm;

import com.example.orderservice.entity.Order;
import com.example.orderservice.external.PaymentService;
import com.example.orderservice.external.ProductService;
import com.example.orderservice.external.request.PaymentRequest;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.request.OrderRequest;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceIpm implements OrderService {

    private final OrderRepository orderRepository;

    private final ProductService productService;

    private final PaymentService paymentService;

    private static final String ORDER_STATUS_CREATE = "CREATE";

    private static final String ORDER_STATUS_FAILED = "PAYMENT_FAILED";

    private static final String ORDER_STATUS_SUCCESS = "PLACED";

    @Override
    @Transactional
    public Long placeOrder(OrderRequest orderRequest) {

        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Create order with status create");

    Order order =   orderRepository.save(Order.builder()
                        .amount(orderRequest.getTotalAmount())
                        .orderStatus(ORDER_STATUS_CREATE)
                        .productId(orderRequest.getProductId())
                        .orderTime(Instant.now())
                        .quantity(orderRequest.getQuantity())
                .build());
        String orderStatus;
        log.info("Call payment service for order service...");
        try{
            paymentService.doPayment(PaymentRequest.builder()
                            .OrderId(order.getId())
                            .paymentMode(orderRequest.getPaymentMode())
                            .amount(orderRequest.getTotalAmount())
                    .build());
            orderStatus = ORDER_STATUS_SUCCESS;
        }
        catch (Exception exception){
            log.error("Error payment order....");
            orderStatus = ORDER_STATUS_FAILED;
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("Add order places successfully with Order: {}  of Product: {}", order.getId(), orderRequest.getProductId());

        return order.getId();
    }
}
