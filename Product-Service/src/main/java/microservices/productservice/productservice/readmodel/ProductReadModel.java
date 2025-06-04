package microservices.productservice.productservice.readmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReadModel {
    
    @Id
    private String id;

    private Long productId;

    private String name;

    private BigDecimal price;

    private Integer quantity;

    private String description;

    private Long categoryId;

    private String categoryName;

    private Long brandId;

    private String brandName;

    private String status;
    
    private Long attributeSetId;
    
    private String attributeSetName;
    
    // Denormalized attribute values for fast queries
    private Map<String, Object> attributes;
    
    // Pre-computed fields for performance
    private List<String> tags;
    
    private Double averageRating;
    
    private Integer reviewCount;
    
    private Boolean isAvailable;
    
    private Boolean isFeatured;
    
    private Boolean isOnSale;
    
    private BigDecimal originalPrice;
    
    private BigDecimal discountPercentage;
    
    // Search optimization
    private List<String> searchKeywords;
    
    // Timestamps
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    private LocalDateTime lastSyncAt;
    
    // Version for optimistic locking
    private Long version;
    
    // Computed methods
    public boolean isInStock() {
        return quantity != null && quantity > 0;
    }
    
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    public BigDecimal getEffectivePrice() {
        if (isOnSale != null && isOnSale && originalPrice != null) {
            return originalPrice;
        }
        return price;
    }
}
