package com.example.orderservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderRequest {

    private long productId;

    private long totalAmount;

    private long quantity;

    private String referenceNumber;

    private PaymentMode paymentMode;
}
