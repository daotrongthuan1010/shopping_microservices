package microservices.productservice.productservice.cqrs.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProductDeletedEvent extends ProductEvent {
    private String reason;
    private String deletedBy;
    
    public ProductDeletedEvent(Long productId, String reason, String deletedBy, Long version) {
        super(productId, "PRODUCT_DELETED", version);
        this.reason = reason;
        this.deletedBy = deletedBy;
    }
}
