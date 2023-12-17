package com.example.orderservice.service.serviceIpm;

import com.example.orderservice.entity.Order;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.external.PaymentService;
import com.example.orderservice.external.ProductService;
import com.example.orderservice.external.request.PaymentRequest;
import com.example.orderservice.external.response.ProductResponse;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.request.OrderRequest;
import com.example.orderservice.response.OrderResponse;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceIpm implements OrderService {

    private final OrderRepository orderRepository;

    private final ProductService productService;

    private final PaymentService paymentService;

    private final RestTemplate restTemplate;

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
                            .referenceNumber(orderRequest.getReferenceNumber())
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

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(long id) {
        log.info("Get order detail...");
     Order order =  orderRepository.findById(id).orElseThrow(()
             -> new OrderNotFoundException("Khong tim thay don dat hang, kiem tra lai ma code","ORDER_NOT_FOUND"));
     log.info("Get product detail...");

     try {
         ProductResponse productResponse =
                 restTemplate.getForObject("http://PRODUCT-SERVICE/product" + order.getProductId(), ProductResponse.class);
         return OrderResponse.builder()
                 .orderId(order.getId())
                 .orderStatus(order.getOrderStatus())
                 .amount(order.getAmount())
                 .orderDate(order.getOrderTime())
                 .productResponse(productResponse)
                 .build();
     }
     catch (Exception exception){
         throw new OrderNotFoundException("Khong tim thay id san pham, vui long kiem tra lai", "NOT_FOUND_PRODUCT");
     }
    }
}
