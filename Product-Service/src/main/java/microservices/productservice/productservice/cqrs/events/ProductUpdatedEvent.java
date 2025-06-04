package microservices.productservice.productservice.cqrs.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProductUpdatedEvent extends ProductEvent {
    private String name;
    private BigDecimal price;
    private String description;
    private Long categoryId;
    private Long brandId;
    private Map<String, Object> attributeValues;
    private String status;
    private Map<String, Object> changedFields;
    
    public ProductUpdatedEvent(Long productId, String name, BigDecimal price, 
                              String description, Long categoryId, Long brandId,
                              Map<String, Object> attributeValues, String status,
                              Map<String, Object> changedFields, Long version) {
        super(productId, "PRODUCT_UPDATED", version);
        this.name = name;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.attributeValues = attributeValues;
        this.status = status;
        this.changedFields = changedFields;
    }
}
