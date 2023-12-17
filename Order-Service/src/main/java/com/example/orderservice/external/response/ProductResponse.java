package com.example.orderservice.external.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class ProductResponse {

    @JsonProperty(namespace = "id")
    private Long id;

    @JsonProperty(namespace = "name")
    private  String name;

    @JsonProperty(namespace = "price")
    private long price;

    @JsonProperty(namespace = "quantity")
    private long quantity;
}
