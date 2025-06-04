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
@Table(name = "attributes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false)
    private DataType dataType;
    
    @Column(name = "is_required")
    @Builder.Default
    private Boolean isRequired = false;
    
    @Column(name = "default_value")
    private String defaultValue;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_set_id")
    private AttributeSet attributeSet;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum DataType {
        TEXT, INTEGER, DECIMAL, DATETIME, VARCHAR
    }
}
