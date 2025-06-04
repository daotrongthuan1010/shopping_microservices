package microservices.productservice.productservice.cqrs.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.productservice.productservice.cqrs.events.*;
import microservices.productservice.productservice.readmodel.ProductReadModel;
import microservices.productservice.productservice.readmodel.ProductSummaryReadModel;
import microservices.productservice.productservice.repository.read.ProductReadRepository;
import microservices.productservice.productservice.repository.read.ProductSummaryReadRepository;
import microservices.productservice.productservice.service.CacheServiceInterface;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventHandler {

    private final ProductReadRepository productReadRepository;
    private final ProductSummaryReadRepository productSummaryReadRepository;
    private final CacheServiceInterface cacheService;
    
    @KafkaListener(topics = "product-events", groupId = "product-service-group")
    public void handleProductEvent(ProductEvent event) {
        log.info("Received event: {} for product: {}", event.getEventType(), event.getProductId());

        try {
            switch (event.getEventType()) {
                case "PRODUCT_CREATED":
                    handleProductCreated((ProductCreatedEvent) event);
                    break;
                case "PRODUCT_UPDATED":
                    handleProductUpdated((ProductUpdatedEvent) event);
                    break;
                case "PRODUCT_DELETED":
                    handleProductDeleted((ProductDeletedEvent) event);
                    break;
                case "PRODUCT_QUANTITY_REDUCED":
                    handleProductQuantityReduced((ProductQuantityReducedEvent) event);
                    break;
                case "PRODUCT_QUANTITY_INCREASED":
                    handleProductQuantityIncreased((ProductQuantityIncreasedEvent) event);
                    break;
                default:
                    log.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error handling event: {} for product: {}",
                    event.getEventType(), event.getProductId(), e);
            // In production, implement dead letter queue or retry mechanism
        }
    }
    
    private void handleProductCreated(ProductCreatedEvent event) {
        log.info("Handling ProductCreatedEvent for product: {}", event.getProductId());
        
        // Create read model
        ProductReadModel readModel = ProductReadModel.builder()
                .productId(event.getProductId())
                .name(event.getName())
                .price(event.getPrice())
                .quantity(event.getQuantity())
                .description(event.getDescription())
                .categoryId(event.getCategoryId())
                .brandId(event.getBrandId())
                .attributeSetId(event.getAttributeSetId())
                .status(event.getStatus())
                .attributes(event.getAttributeValues())
                .isAvailable(event.getQuantity() > 0 && "ACTIVE".equals(event.getStatus()))
                .isFeatured(false)
                .isOnSale(false)
                .searchKeywords(generateSearchKeywords(event.getName(), event.getDescription()))
                .createdAt(event.getTimestamp())
                .updatedAt(event.getTimestamp())
                .lastSyncAt(LocalDateTime.now())
                .version(event.getVersion())
                .build();
        
        productReadRepository.save(readModel);
        
        // Create summary read model
        ProductSummaryReadModel summaryModel = ProductSummaryReadModel.builder()
                .productId(event.getProductId())
                .name(event.getName())
                .price(event.getPrice())
                .quantity(event.getQuantity())
                .categoryId(event.getCategoryId())
                .brandId(event.getBrandId())
                .status(event.getStatus())
                .isAvailable(event.getQuantity() > 0 && "ACTIVE".equals(event.getStatus()))
                .isFeatured(false)
                .createdAt(event.getTimestamp())
                .updatedAt(event.getTimestamp())
                .build();
        
        productSummaryReadRepository.save(summaryModel);
        
        // Invalidate cache
        cacheService.evictProductCache(event.getProductId());
        
        log.info("Product read models created for product: {}", event.getProductId());
    }
    
    private void handleProductUpdated(ProductUpdatedEvent event) {
        log.info("Handling ProductUpdatedEvent for product: {}", event.getProductId());
        
        // Update read model
        Optional<ProductReadModel> readModelOpt = productReadRepository.findByProductId(event.getProductId());
        if (readModelOpt.isPresent()) {
            ProductReadModel readModel = readModelOpt.get();
            
            readModel.setName(event.getName());
            readModel.setPrice(event.getPrice());
            readModel.setDescription(event.getDescription());
            readModel.setCategoryId(event.getCategoryId());
            readModel.setBrandId(event.getBrandId());
            readModel.setStatus(event.getStatus());
            readModel.setAttributes(event.getAttributeValues());
            readModel.setIsAvailable(readModel.getQuantity() > 0 && "ACTIVE".equals(event.getStatus()));
            readModel.setSearchKeywords(generateSearchKeywords(event.getName(), event.getDescription()));
            readModel.setUpdatedAt(event.getTimestamp());
            readModel.setLastSyncAt(LocalDateTime.now());
            readModel.setVersion(event.getVersion());
            
            productReadRepository.save(readModel);
        }
        
        // Update summary read model
        Optional<ProductSummaryReadModel> summaryModelOpt = productSummaryReadRepository.findByProductId(event.getProductId());
        if (summaryModelOpt.isPresent()) {
            ProductSummaryReadModel summaryModel = summaryModelOpt.get();
            
            summaryModel.setName(event.getName());
            summaryModel.setPrice(event.getPrice());
            summaryModel.setCategoryId(event.getCategoryId());
            summaryModel.setBrandId(event.getBrandId());
            summaryModel.setStatus(event.getStatus());
            summaryModel.setUpdatedAt(event.getTimestamp());
            
            productSummaryReadRepository.save(summaryModel);
        }
        
        // Invalidate cache
        cacheService.evictProductCache(event.getProductId());
        
        log.info("Product read models updated for product: {}", event.getProductId());
    }
    
    private void handleProductDeleted(ProductDeletedEvent event) {
        log.info("Handling ProductDeletedEvent for product: {}", event.getProductId());
        
        // Remove from read repositories
        productReadRepository.findByProductId(event.getProductId())
                .ifPresent(productReadRepository::delete);
        
        productSummaryReadRepository.findByProductId(event.getProductId())
                .ifPresent(productSummaryReadRepository::delete);
        
        // Invalidate cache
        cacheService.evictProductCache(event.getProductId());
        
        log.info("Product read models deleted for product: {}", event.getProductId());
    }
    
    private void handleProductQuantityReduced(ProductQuantityReducedEvent event) {
        log.info("Handling ProductQuantityReducedEvent for product: {}", event.getProductId());
        
        updateQuantityInReadModels(event.getProductId(), event.getRemainingQuantity(), event.getTimestamp(), event.getVersion());
    }
    
    private void handleProductQuantityIncreased(ProductQuantityIncreasedEvent event) {
        log.info("Handling ProductQuantityIncreasedEvent for product: {}", event.getProductId());
        
        updateQuantityInReadModels(event.getProductId(), event.getNewQuantity(), event.getTimestamp(), event.getVersion());
    }
    
    private void updateQuantityInReadModels(Long productId, Integer newQuantity, LocalDateTime timestamp, Long version) {
        // Update read model
        Optional<ProductReadModel> readModelOpt = productReadRepository.findByProductId(productId);
        if (readModelOpt.isPresent()) {
            ProductReadModel readModel = readModelOpt.get();
            readModel.setQuantity(newQuantity);
            readModel.setIsAvailable(newQuantity > 0 && "ACTIVE".equals(readModel.getStatus()));
            readModel.setUpdatedAt(timestamp);
            readModel.setLastSyncAt(LocalDateTime.now());
            readModel.setVersion(version);
            
            productReadRepository.save(readModel);
        }
        
        // Update summary read model
        Optional<ProductSummaryReadModel> summaryModelOpt = productSummaryReadRepository.findByProductId(productId);
        if (summaryModelOpt.isPresent()) {
            ProductSummaryReadModel summaryModel = summaryModelOpt.get();
            summaryModel.setQuantity(newQuantity);
            summaryModel.setIsAvailable(newQuantity > 0 && "ACTIVE".equals(summaryModel.getStatus()));
            summaryModel.setUpdatedAt(timestamp);
            
            productSummaryReadRepository.save(summaryModel);
        }
        
        // Invalidate cache
        cacheService.evictProductCache(productId);
        
        log.info("Quantity updated in read models for product: {} -> {}", productId, newQuantity);
    }
    
    private java.util.List<String> generateSearchKeywords(String name, String description) {
        java.util.List<String> keywords = new ArrayList<>();
        
        if (name != null) {
            keywords.addAll(java.util.Arrays.asList(name.toLowerCase().split("\\s+")));
        }
        
        if (description != null) {
            keywords.addAll(java.util.Arrays.asList(description.toLowerCase().split("\\s+")));
        }
        
        return keywords.stream().distinct().collect(java.util.stream.Collectors.toList());
    }
}
