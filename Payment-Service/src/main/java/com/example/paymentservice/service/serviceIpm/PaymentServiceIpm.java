package com.example.paymentservice.service.serviceIpm;

import com.example.paymentservice.entity.TransactionDetails;
import com.example.paymentservice.model.PaymentRequest;
import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceIpm implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public long addPayment(PaymentRequest request) {
        log.info("Start save repository payment with orderId: {}", request.getOrderId());
        paymentRepository.save(TransactionDetails.builder()
                        .paymentDate(Instant.now())
                        .paymentMode(request.getPaymentMode().name())
                        .paymentStatus("OK")
                        .orderId(request.getOrderId())
                        .referenceNumber(request.getReferenceNumber())
                        .amount(request.getAmount())
                        .build());
        return 0;
    }
}
