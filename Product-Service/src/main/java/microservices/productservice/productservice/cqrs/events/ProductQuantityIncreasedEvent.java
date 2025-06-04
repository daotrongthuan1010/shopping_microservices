package microservices.productservice.productservice.cqrs.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProductQuantityIncreasedEvent extends ProductEvent {
    private Integer addedQuantity;
    private Integer newQuantity;
    private String reason;
    
    public ProductQuantityIncreasedEvent(Long productId, Integer addedQuantity, 
                                       Integer newQuantity, String reason, Long version) {
        super(productId, "PRODUCT_QUANTITY_INCREASED", version);
        this.addedQuantity = addedQuantity;
        this.newQuantity = newQuantity;
        this.reason = reason;
    }
}
