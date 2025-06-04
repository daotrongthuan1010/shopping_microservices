package com.example.orderservice.controller;

import com.example.orderservice.request.OrderRequest;
import com.example.orderservice.response.OrderResponse;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    @PreAuthorize("hasAuthority('Customer')")
    @PostMapping("/add")
    public ResponseEntity<Long> placeOrder(@RequestBody  OrderRequest orderRequest){
        long orderId = orderService.placeOrder(orderRequest);

        log.info("Order Id: {}", orderId);
        return new ResponseEntity<>(orderId, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer')")
    @GetMapping("/get/{id}")
    public ResponseEntity<OrderResponse> placeOrder(@PathVariable long id ){
        OrderResponse orderResponse = orderService.findById(id);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }



}
