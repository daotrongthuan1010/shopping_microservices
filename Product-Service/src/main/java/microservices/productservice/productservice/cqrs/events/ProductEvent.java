package microservices.productservice.productservice.cqrs.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "eventType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ProductCreatedEvent.class, name = "PRODUCT_CREATED"),
    @JsonSubTypes.Type(value = ProductUpdatedEvent.class, name = "PRODUCT_UPDATED"),
    @JsonSubTypes.Type(value = ProductDeletedEvent.class, name = "PRODUCT_DELETED"),
    @JsonSubTypes.Type(value = ProductQuantityReducedEvent.class, name = "PRODUCT_QUANTITY_REDUCED"),
    @JsonSubTypes.Type(value = ProductQuantityIncreasedEvent.class, name = "PRODUCT_QUANTITY_INCREASED")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class ProductEvent {
    private String eventId;
    private Long productId;
    private LocalDateTime timestamp;
    private String eventType;
    private Long version;
    
    public ProductEvent(Long productId, String eventType, Long version) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.productId = productId;
        this.eventType = eventType;
        this.version = version;
        this.timestamp = LocalDateTime.now();
    }
}
