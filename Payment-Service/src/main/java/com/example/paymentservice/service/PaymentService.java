package com.example.paymentservice.service;

import com.example.paymentservice.model.PaymentRequest;

public interface PaymentService {
    long addPayment(PaymentRequest request);
}
