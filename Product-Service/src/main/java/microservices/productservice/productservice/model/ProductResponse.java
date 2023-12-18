package microservices.productservice.productservice.model;

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
    private Long id;

    @JsonProperty(namespace = "name")
    private  String name;

    @JsonProperty(namespace = "price")
    private long price;

    @JsonProperty(namespace = "quantity")
    private long quantity;
}
