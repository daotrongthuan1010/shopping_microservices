package com.example.paymentservice.service;

import com.example.paymentservice.model.PaymentRequest;
import com.example.paymentservice.repsonse.PaymentResponse;

public interface PaymentService {
    long addPayment(PaymentRequest request);

    PaymentResponse findById(Long id);
}
