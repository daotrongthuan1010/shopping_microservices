package microservices.productservice.productservice.readmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "product_summaries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryReadModel {
    
    @Id
    private String id;

    private Long productId;

    private String name;

    private BigDecimal price;

    private Integer quantity;

    private Long categoryId;

    private String categoryName;

    private Long brandId;

    private String brandName;

    private String status;

    private String imageUrl;

    private Double averageRating;

    private Integer reviewCount;

    private Boolean isAvailable;

    private Boolean isFeatured;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
