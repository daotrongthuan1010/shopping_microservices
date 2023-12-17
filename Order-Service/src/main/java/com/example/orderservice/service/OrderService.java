package com.example.orderservice.service;

import com.example.orderservice.entity.Order;
import com.example.orderservice.request.OrderRequest;
import com.example.orderservice.response.OrderResponse;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);

    OrderResponse findById(long id);
}
