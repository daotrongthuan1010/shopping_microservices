package microservices.productservice.productservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "attribute_set_id")
    private Long attributeSetId;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AttributeValue> attributeValues = new ArrayList<>();
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "category_id")
    private Long categoryId;
    
    @Column(name = "brand_id")
    private Long brandId;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    public enum ProductStatus {
        ACTIVE, INACTIVE, DISCONTINUED
    }
    
    // Business methods
    public void reduceQuantity(int quantity) {
        if (this.quantity < quantity) {
            throw new IllegalArgumentException("Insufficient quantity");
        }
        this.quantity -= quantity;
    }
    
    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }
    
    public boolean isAvailable() {
        return status == ProductStatus.ACTIVE && quantity > 0;
    }
}
