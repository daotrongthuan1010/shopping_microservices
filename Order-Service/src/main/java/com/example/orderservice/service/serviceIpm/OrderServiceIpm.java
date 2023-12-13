package com.example.orderservice.service.serviceIpm;

import com.example.orderservice.entity.Order;
import com.example.orderservice.external.ProductService;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.request.OrderRequest;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceIpm implements OrderService {

    private final OrderRepository orderRepository;

    private final ProductService productService;

    @Override
    public long placeOrder(OrderRequest orderRequest) {

        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Create order with status create");

    Order order =   orderRepository.save(Order.builder()
                        .amount(orderRequest.getTotalAmount())
                        .orderStatus("CREATE")
                        .productId(orderRequest.getProductId())
                        .orderTime(Instant.now())
                        .quantity(orderRequest.getQuantity())
                .build());
        log.info("Add order places successfully with Order: {}  of Product: {}", order.getId(), orderRequest.getProductId());
        return order.getId();
    }
}
