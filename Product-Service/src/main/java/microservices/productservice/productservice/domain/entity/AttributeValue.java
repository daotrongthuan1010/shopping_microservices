package microservices.productservice.productservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "attribute_values")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeValue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "attribute_code", nullable = false)
    private String attributeCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValueType type;
    
    @Column(columnDefinition = "TEXT")
    private String value;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ValueType {
        INT_ATTRIBUTE_VALUE,
        DECIMAL_ATTRIBUTE_VALUE,
        DATETIME_ATTRIBUTE_VALUE,
        TEXT_ATTRIBUTE_VALUE,
        VARCHAR_ATTRIBUTE_VALUE
    }
    
    // Utility methods for type-safe value access
    public Integer getIntValue() {
        if (type == ValueType.INT_ATTRIBUTE_VALUE && value != null) {
            return Integer.valueOf(value);
        }
        return null;
    }
    
    public Double getDecimalValue() {
        if (type == ValueType.DECIMAL_ATTRIBUTE_VALUE && value != null) {
            return Double.valueOf(value);
        }
        return null;
    }
    
    public LocalDateTime getDateTimeValue() {
        if (type == ValueType.DATETIME_ATTRIBUTE_VALUE && value != null) {
            return LocalDateTime.parse(value);
        }
        return null;
    }
    
    public String getStringValue() {
        return value;
    }
}
