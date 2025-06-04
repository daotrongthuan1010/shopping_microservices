package microservices.productservice.productservice.cqrs.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.productservice.productservice.cqrs.commands.CreateProductCommand;
import microservices.productservice.productservice.cqrs.commands.ReduceProductQuantityCommand;
import microservices.productservice.productservice.cqrs.commands.UpdateProductCommand;
import microservices.productservice.productservice.cqrs.events.*;
import microservices.productservice.productservice.domain.entity.AttributeValue;
import microservices.productservice.productservice.domain.entity.Product;
import microservices.productservice.productservice.repository.write.ProductWriteRepository;
import microservices.productservice.productservice.service.EventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCommandHandler {
    
    private final ProductWriteRepository productWriteRepository;
    private final EventPublisher eventPublisher;
    
    @Transactional
    public Long handle(CreateProductCommand command) {
        log.info("Handling CreateProductCommand for product: {}", command.getName());
        
        try {
            // Create product entity
            Product product = Product.builder()
                    .name(command.getName())
                    .price(command.getPrice())
                    .quantity(command.getQuantity())
                    .description(command.getDescription())
                    .categoryId(command.getCategoryId())
                    .brandId(command.getBrandId())
                    .attributeSetId(command.getAttributeSetId())
                    .status(Product.ProductStatus.valueOf(command.getStatus()))
                    .build();
            
            // Save to write database (PostgreSQL)
            Product savedProduct = productWriteRepository.save(product);
            
            // Create attribute values if provided
            if (command.getAttributeValues() != null && !command.getAttributeValues().isEmpty()) {
                createAttributeValues(savedProduct, command.getAttributeValues());
                savedProduct = productWriteRepository.save(savedProduct);
            }
            
            // Publish event for read model synchronization
            ProductCreatedEvent event = new ProductCreatedEvent(
                    savedProduct.getId(),
                    savedProduct.getName(),
                    savedProduct.getPrice(),
                    savedProduct.getQuantity(),
                    savedProduct.getDescription(),
                    savedProduct.getCategoryId(),
                    savedProduct.getBrandId(),
                    savedProduct.getAttributeSetId(),
                    command.getAttributeValues(),
                    savedProduct.getStatus().name(),
                    savedProduct.getVersion()
            );
            
            eventPublisher.publishEvent(event);
            
            log.info("Product created successfully with ID: {}", savedProduct.getId());
            return savedProduct.getId();
            
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create product", e);
        }
    }
    
    @Transactional
    public void handle(UpdateProductCommand command) {
        log.info("Handling UpdateProductCommand for product ID: {}", command.getProductId());
        
        try {
            // Find product with optimistic locking
            Product product = productWriteRepository.findByIdAndVersion(
                    command.getProductId(), command.getVersion())
                    .orElseThrow(() -> new RuntimeException("Product not found or version mismatch"));
            
            // Track changed fields
            Map<String, Object> changedFields = new HashMap<>();
            
            // Update fields if provided
            if (command.getName() != null && !command.getName().equals(product.getName())) {
                changedFields.put("name", Map.of("old", product.getName(), "new", command.getName()));
                product.setName(command.getName());
            }
            
            if (command.getPrice() != null && !command.getPrice().equals(product.getPrice())) {
                changedFields.put("price", Map.of("old", product.getPrice(), "new", command.getPrice()));
                product.setPrice(command.getPrice());
            }
            
            if (command.getDescription() != null && !command.getDescription().equals(product.getDescription())) {
                changedFields.put("description", Map.of("old", product.getDescription(), "new", command.getDescription()));
                product.setDescription(command.getDescription());
            }
            
            if (command.getCategoryId() != null && !command.getCategoryId().equals(product.getCategoryId())) {
                changedFields.put("categoryId", Map.of("old", product.getCategoryId(), "new", command.getCategoryId()));
                product.setCategoryId(command.getCategoryId());
            }
            
            if (command.getBrandId() != null && !command.getBrandId().equals(product.getBrandId())) {
                changedFields.put("brandId", Map.of("old", product.getBrandId(), "new", command.getBrandId()));
                product.setBrandId(command.getBrandId());
            }
            
            if (command.getStatus() != null && !command.getStatus().equals(product.getStatus().name())) {
                changedFields.put("status", Map.of("old", product.getStatus().name(), "new", command.getStatus()));
                product.setStatus(Product.ProductStatus.valueOf(command.getStatus()));
            }
            
            // Update attribute values if provided
            if (command.getAttributeValues() != null) {
                updateAttributeValues(product, command.getAttributeValues());
                changedFields.put("attributeValues", command.getAttributeValues());
            }
            
            // Save updated product
            Product updatedProduct = productWriteRepository.save(product);
            
            // Publish update event
            ProductUpdatedEvent event = new ProductUpdatedEvent(
                    updatedProduct.getId(),
                    updatedProduct.getName(),
                    updatedProduct.getPrice(),
                    updatedProduct.getDescription(),
                    updatedProduct.getCategoryId(),
                    updatedProduct.getBrandId(),
                    command.getAttributeValues(),
                    updatedProduct.getStatus().name(),
                    changedFields,
                    updatedProduct.getVersion()
            );
            
            eventPublisher.publishEvent(event);
            
            log.info("Product updated successfully: {}", updatedProduct.getId());
            
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update product", e);
        }
    }
    
    @Transactional
    public void handle(ReduceProductQuantityCommand command) {
        log.info("Handling ReduceProductQuantityCommand for product ID: {}", command.getProductId());
        
        try {
            // Use optimistic locking for quantity reduction
            int updatedRows = productWriteRepository.reduceQuantity(
                    command.getProductId(), 
                    command.getQuantity(), 
                    command.getVersion()
            );
            
            if (updatedRows == 0) {
                throw new RuntimeException("Failed to reduce quantity - insufficient stock or version mismatch");
            }
            
            // Get updated product
            Product product = productWriteRepository.findById(command.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            
            // Publish quantity reduced event
            ProductQuantityReducedEvent event = new ProductQuantityReducedEvent(
                    product.getId(),
                    command.getQuantity(),
                    product.getQuantity(),
                    command.getReason(),
                    product.getVersion()
            );
            
            eventPublisher.publishEvent(event);
            
            log.info("Product quantity reduced successfully: {} -> {}", 
                    product.getQuantity() + command.getQuantity(), product.getQuantity());
            
        } catch (Exception e) {
            log.error("Error reducing product quantity: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to reduce product quantity", e);
        }
    }
    
    private void createAttributeValues(Product product, Map<String, Object> attributeValues) {
        attributeValues.forEach((key, value) -> {
            AttributeValue attributeValue = AttributeValue.builder()
                    .attributeCode(key)
                    .value(value.toString())
                    .type(determineValueType(value))
                    .product(product)
                    .build();
            product.getAttributeValues().add(attributeValue);
        });
    }
    
    private void updateAttributeValues(Product product, Map<String, Object> attributeValues) {
        // Clear existing attribute values
        product.getAttributeValues().clear();
        
        // Add new attribute values
        createAttributeValues(product, attributeValues);
    }
    
    private AttributeValue.ValueType determineValueType(Object value) {
        if (value instanceof Integer) {
            return AttributeValue.ValueType.INT_ATTRIBUTE_VALUE;
        } else if (value instanceof Double || value instanceof Float) {
            return AttributeValue.ValueType.DECIMAL_ATTRIBUTE_VALUE;
        } else if (value.toString().matches("\\d{4}-\\d{2}-\\d{2}.*")) {
            return AttributeValue.ValueType.DATETIME_ATTRIBUTE_VALUE;
        } else if (value.toString().length() > 255) {
            return AttributeValue.ValueType.TEXT_ATTRIBUTE_VALUE;
        } else {
            return AttributeValue.ValueType.VARCHAR_ATTRIBUTE_VALUE;
        }
    }
}
