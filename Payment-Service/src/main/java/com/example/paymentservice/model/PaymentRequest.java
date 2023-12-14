package com.example.paymentservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequest {

    private long OrderId;

    private long amount;

    private String referenceNumber;

    private PaymentMode paymentMode;

}
