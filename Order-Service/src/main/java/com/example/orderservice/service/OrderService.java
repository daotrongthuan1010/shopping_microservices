package com.example.orderservice.service;

import com.example.orderservice.request.OrderRequest;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

}
