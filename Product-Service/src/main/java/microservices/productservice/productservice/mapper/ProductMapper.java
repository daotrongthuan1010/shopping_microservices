package microservices.productservice.productservice.mapper;

import microservices.productservice.productservice.cqrs.commands.CreateProductCommand;
import microservices.productservice.productservice.cqrs.commands.ReduceProductQuantityCommand;
import microservices.productservice.productservice.cqrs.commands.UpdateProductCommand;
import microservices.productservice.productservice.domain.entity.Product;
import microservices.productservice.productservice.dto.request.CreateProductRequest;
import microservices.productservice.productservice.dto.request.UpdateProductRequest;
import microservices.productservice.productservice.dto.request.UpdateQuantityRequest;
import microservices.productservice.productservice.dto.response.ProductResponse;
import microservices.productservice.productservice.dto.response.ProductSummaryResponse;
import microservices.productservice.productservice.readmodel.ProductReadModel;
import microservices.productservice.productservice.readmodel.ProductSummaryReadModel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProductMapper {
    
    // Request to Command mappings
    public CreateProductCommand toCommand(CreateProductRequest request) {
        return CreateProductCommand.builder()
                .name(request.getName())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .brandId(request.getBrandId())
                .attributeSetId(request.getAttributeSetId())
                .attributeValues(request.getAttributeValues())
                .status(request.getStatus())
                .build();
    }
    
    public UpdateProductCommand toCommand(Long productId, UpdateProductRequest request) {
        return UpdateProductCommand.builder()
                .productId(productId)
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .brandId(request.getBrandId())
                .attributeValues(request.getAttributeValues())
                .status(request.getStatus())
                .version(request.getVersion())
                .build();
    }
    
    public ReduceProductQuantityCommand toReduceQuantityCommand(Long productId, UpdateQuantityRequest request) {
        return ReduceProductQuantityCommand.builder()
                .productId(productId)
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .version(request.getVersion())
                .build();
    }
    
    // Entity to Response mappings
    public ProductResponse toResponse(Product product) {
        Map<String, Object> attributes = new HashMap<>();
        if (product.getAttributeValues() != null) {
            product.getAttributeValues().forEach(attr -> 
                attributes.put(attr.getAttributeCode(), attr.getValue())
            );
        }
        
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .description(product.getDescription())
                .categoryId(product.getCategoryId())
                .brandId(product.getBrandId())
                .attributeSetId(product.getAttributeSetId())
                .status(product.getStatus().name())
                .attributes(attributes)
                .isAvailable(product.isAvailable())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .version(product.getVersion())
                .build();
    }
    
    // Read Model to Response mappings
    public ProductResponse toResponse(ProductReadModel readModel) {
        return ProductResponse.builder()
                .id(readModel.getProductId())
                .name(readModel.getName())
                .price(readModel.getPrice())
                .quantity(readModel.getQuantity())
                .description(readModel.getDescription())
                .categoryId(readModel.getCategoryId())
                .categoryName(readModel.getCategoryName())
                .brandId(readModel.getBrandId())
                .brandName(readModel.getBrandName())
                .attributeSetId(readModel.getAttributeSetId())
                .attributeSetName(readModel.getAttributeSetName())
                .status(readModel.getStatus())
                .attributes(readModel.getAttributes())
                .tags(readModel.getTags())
                .averageRating(readModel.getAverageRating())
                .reviewCount(readModel.getReviewCount())
                .isAvailable(readModel.getIsAvailable())
                .isFeatured(readModel.getIsFeatured())
                .isOnSale(readModel.getIsOnSale())
                .originalPrice(readModel.getOriginalPrice())
                .discountPercentage(readModel.getDiscountPercentage())
                .createdAt(readModel.getCreatedAt())
                .updatedAt(readModel.getUpdatedAt())
                .version(readModel.getVersion())
                .build();
    }
    
    public ProductSummaryResponse toSummaryResponse(ProductSummaryReadModel summaryModel) {
        return ProductSummaryResponse.builder()
                .id(summaryModel.getProductId())
                .name(summaryModel.getName())
                .price(summaryModel.getPrice())
                .quantity(summaryModel.getQuantity())
                .categoryId(summaryModel.getCategoryId())
                .categoryName(summaryModel.getCategoryName())
                .brandId(summaryModel.getBrandId())
                .brandName(summaryModel.getBrandName())
                .status(summaryModel.getStatus())
                .imageUrl(summaryModel.getImageUrl())
                .averageRating(summaryModel.getAverageRating())
                .reviewCount(summaryModel.getReviewCount())
                .isAvailable(summaryModel.getIsAvailable())
                .isFeatured(summaryModel.getIsFeatured())
                .createdAt(summaryModel.getCreatedAt())
                .updatedAt(summaryModel.getUpdatedAt())
                .build();
    }
}
