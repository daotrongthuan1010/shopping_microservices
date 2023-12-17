package com.example.orderservice.response;

import com.example.orderservice.external.response.ProductResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    @JsonProperty(namespace = "id")
    private long orderId;

    @JsonProperty(namespace = "order_date")
    private Instant orderDate;

    @JsonProperty(namespace = "order_status")
    private String orderStatus;

    @JsonProperty(namespace = "amount")
    private long amount;

    @JsonProperty(namespace = "product")
    private ProductResponse productResponse;

}
