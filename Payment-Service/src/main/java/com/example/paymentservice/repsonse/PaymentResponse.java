package com.example.paymentservice.repsonse;

import com.example.paymentservice.model.PaymentMode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PaymentResponse {

    @JsonProperty(namespace = "paymentId")
    private long paymentId;

    @JsonProperty(namespace = "status")
    private String status;

    @JsonProperty(namespace = "payment_mode")
    private PaymentMode paymentMode;

    @JsonProperty(namespace = "amount")
    private long amount;

    @JsonProperty(namespace = "payment_date")
    private Instant paymentDate;

    @JsonProperty(namespace = "order_id")
    private long orderId;
}
