package com.example.orderservice.external.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    @JsonProperty(namespace = "id")
    private String id;

    @JsonProperty(namespace = "name")
    private  String name;

    @JsonProperty(namespace = "price")
    private String price;

    @JsonProperty(namespace = "quantity")
    private String quantity;
}
