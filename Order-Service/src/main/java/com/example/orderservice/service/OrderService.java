package com.example.orderservice.service;

import com.example.orderservice.entity.Order;
import com.example.orderservice.request.OrderRequest;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);

}
