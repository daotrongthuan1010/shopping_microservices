package microservices.productservice.productservice.cqrs.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProductQuantityReducedEvent extends ProductEvent {
    private Integer reducedQuantity;
    private Integer remainingQuantity;
    private String reason;
    
    public ProductQuantityReducedEvent(Long productId, Integer reducedQuantity, 
                                     Integer remainingQuantity, String reason, Long version) {
        super(productId, "PRODUCT_QUANTITY_REDUCED", version);
        this.reducedQuantity = reducedQuantity;
        this.remainingQuantity = remainingQuantity;
        this.reason = reason;
    }
}
