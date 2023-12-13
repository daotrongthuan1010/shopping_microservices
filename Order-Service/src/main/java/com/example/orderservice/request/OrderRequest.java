package com.example.orderservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderRequest {

    private long productId;

    private long totalAmount;

    private long quantity;

    private PaymentMode paymentMode;
}
