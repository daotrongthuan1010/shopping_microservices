package com.example.orderservice.controller;

import com.example.orderservice.request.OrderRequest;
import com.example.orderservice.response.OrderResponse;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/add")
    public ResponseEntity<Long> placeOrder(@RequestBody  OrderRequest orderRequest){
        long orderId = orderService.placeOrder(orderRequest);
        log.info("Order Id: {}", orderId);
        return new ResponseEntity<>(orderId, HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<OrderResponse> placeOrder(@PathVariable long id ){
        OrderResponse orderResponse = orderService.findById(id);

        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

}
