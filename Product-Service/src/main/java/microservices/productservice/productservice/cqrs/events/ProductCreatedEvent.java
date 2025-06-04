package microservices.productservice.productservice.cqrs.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProductCreatedEvent extends ProductEvent {
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String description;
    private Long categoryId;
    private Long brandId;
    private Long attributeSetId;
    private Map<String, Object> attributeValues;
    private String status;
    
    public ProductCreatedEvent(Long productId, String name, BigDecimal price, 
                              Integer quantity, String description, Long categoryId, 
                              Long brandId, Long attributeSetId, 
                              Map<String, Object> attributeValues, String status, Long version) {
        super(productId, "PRODUCT_CREATED", version);
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.attributeSetId = attributeSetId;
        this.attributeValues = attributeValues;
        this.status = status;
    }
}
