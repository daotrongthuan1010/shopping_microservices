package microservices.productservice.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.productservice.productservice.readmodel.ProductReadModel;
import microservices.productservice.productservice.readmodel.ProductSummaryReadModel;
import microservices.productservice.productservice.repository.read.ProductReadRepository;
import microservices.productservice.productservice.repository.read.ProductSummaryReadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductQueryService {

    private final ProductReadRepository productReadRepository;
    private final ProductSummaryReadRepository productSummaryReadRepository;
    private final CacheServiceInterface cacheService;

    // Helper methods
    private <T> Page<T> emptyPage(Pageable pageable) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    private boolean isMongoAvailable() {
        return productReadRepository != null && productSummaryReadRepository != null;
    }
    
    // Single product queries
    public Optional<ProductReadModel> findById(Long productId) {
        log.debug("Finding product by ID: {}", productId);

        // Try cache first
        ProductReadModel cached = cacheService.getProductFromCache(productId);
        if (cached != null) {
            log.debug("Product found in cache: {}", productId);
            return Optional.of(cached);
        }

        // Query from database
        Optional<ProductReadModel> product = productReadRepository.findByProductId(productId);

        // Cache the result if found
        product.ifPresent(p -> cacheService.cacheProduct(productId, p));

        return product;
    }
    
    public Optional<ProductSummaryReadModel> findSummaryById(Long productId) {
        log.debug("Finding product summary by ID: {}", productId);

        // Try cache first
        ProductSummaryReadModel cached = cacheService.getProductSummaryFromCache(productId);
        if (cached != null) {
            log.debug("Product summary found in cache: {}", productId);
            return Optional.of(cached);
        }

        // Query from database
        Optional<ProductSummaryReadModel> summary = productSummaryReadRepository.findByProductId(productId);

        // Cache the result if found
        summary.ifPresent(s -> cacheService.cacheProductSummary(productId, s));

        return summary;
    }
    
    // List queries with caching
    public Page<ProductReadModel> findActiveProducts(Pageable pageable) {
        log.debug("Finding active products with pagination: {}", pageable);
        return productReadRepository.findActiveProducts(pageable);
    }

    public Page<ProductSummaryReadModel> findActiveProductSummaries(Pageable pageable) {
        log.debug("Finding active product summaries with pagination: {}", pageable);
        return productSummaryReadRepository.findActiveProducts(pageable);
    }
    
    // Category-based queries
    public Page<ProductReadModel> findByCategory(Long categoryId, Pageable pageable) {
        log.debug("Finding products by category: {} with pagination: {}", categoryId, pageable);
        return productReadRepository.findByCategoryIdAndActive(categoryId, pageable);
    }
    
    public Page<ProductSummaryReadModel> findSummariesByCategory(Long categoryId, Pageable pageable) {
        log.debug("Finding product summaries by category: {} with pagination: {}", categoryId, pageable);
        
        // Try cache first for category products
        List<ProductSummaryReadModel> cached = cacheService.getCategoryProductsFromCache(categoryId);
        if (cached != null && !cached.isEmpty()) {
            log.debug("Category products found in cache: {}", categoryId);
            // Convert list to page (simplified - in production you'd handle pagination properly)
            return productSummaryReadRepository.findByCategoryIdAndActive(categoryId, pageable);
        }
        
        Page<ProductSummaryReadModel> result = productSummaryReadRepository.findByCategoryIdAndActive(categoryId, pageable);
        
        // Cache the results
        if (!result.isEmpty()) {
            cacheService.cacheCategoryProducts(categoryId, result.getContent());
        }
        
        return result;
    }
    
    // Brand-based queries
    public Page<ProductReadModel> findByBrand(Long brandId, Pageable pageable) {
        log.debug("Finding products by brand: {} with pagination: {}", brandId, pageable);
        return productReadRepository.findByBrandIdAndActive(brandId, pageable);
    }
    
    public Page<ProductSummaryReadModel> findSummariesByBrand(Long brandId, Pageable pageable) {
        log.debug("Finding product summaries by brand: {} with pagination: {}", brandId, pageable);
        
        // Try cache first
        List<ProductSummaryReadModel> cached = cacheService.getBrandProductsFromCache(brandId);
        if (cached != null && !cached.isEmpty()) {
            log.debug("Brand products found in cache: {}", brandId);
            return productSummaryReadRepository.findByBrandIdAndActive(brandId, pageable);
        }
        
        Page<ProductSummaryReadModel> result = productSummaryReadRepository.findByBrandIdAndActive(brandId, pageable);
        
        // Cache the results
        if (!result.isEmpty()) {
            cacheService.cacheBrandProducts(brandId, result.getContent());
        }
        
        return result;
    }
    
    // Price range queries
    public Page<ProductReadModel> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("Finding products by price range: {} - {} with pagination: {}", minPrice, maxPrice, pageable);
        return productReadRepository.findByPriceRangeAndActive(minPrice, maxPrice, pageable);
    }
    
    // Search queries
    public Page<ProductReadModel> searchProducts(String searchText, Pageable pageable) {
        log.debug("Searching products with text: '{}' and pagination: {}", searchText, pageable);
        return productReadRepository.findByTextSearchAndActive(searchText, pageable);
    }
    
    // Special queries
    public Page<ProductReadModel> findAvailableProducts(Pageable pageable) {
        log.debug("Finding available products with pagination: {}", pageable);
        return productReadRepository.findAvailableProducts(pageable);
    }
    
    public List<ProductSummaryReadModel> findFeaturedProducts() {
        log.debug("Finding featured products");
        
        // Try cache first
        List<ProductSummaryReadModel> cached = cacheService.getFeaturedProductsFromCache();
        if (cached != null) {
            log.debug("Featured products found in cache");
            return cached;
        }
        
        // Query from database
        List<ProductSummaryReadModel> featured = productSummaryReadRepository.findFeaturedProducts();
        
        // Cache the results
        if (!featured.isEmpty()) {
            cacheService.cacheFeaturedProducts(featured);
        }
        
        return featured;
    }
    
    public Page<ProductReadModel> findProductsOnSale(Pageable pageable) {
        log.debug("Finding products on sale with pagination: {}", pageable);
        return productReadRepository.findProductsOnSale(pageable);
    }
    
    // Complex filter queries
    public Page<ProductReadModel> findByFilters(List<Long> categoryIds, List<Long> brandIds, 
                                               BigDecimal minPrice, BigDecimal maxPrice, 
                                               Pageable pageable) {
        log.debug("Finding products by filters - categories: {}, brands: {}, price: {}-{}", 
                categoryIds, brandIds, minPrice, maxPrice);
        return productReadRepository.findByFilters(categoryIds, brandIds, minPrice, maxPrice, pageable);
    }
    
    // Attribute-based queries
    public Page<ProductReadModel> findByAttributeValue(String attributeKey, Object attributeValue, Pageable pageable) {
        log.debug("Finding products by attribute: {} = {}", attributeKey, attributeValue);
        return productReadRepository.findByAttributeValue(attributeKey, attributeValue, pageable);
    }
    
    // Statistics queries
    public Long countByCategory(Long categoryId) {
        log.debug("Counting products by category: {}", categoryId);
        return productReadRepository.countByCategoryIdAndActive(categoryId);
    }
    
    public Long countByBrand(Long brandId) {
        log.debug("Counting products by brand: {}", brandId);
        return productReadRepository.countByBrandIdAndActive(brandId);
    }
    
    // Low stock alerts
    public List<ProductReadModel> findLowStockProducts(Integer threshold) {
        log.debug("Finding low stock products with threshold: {}", threshold);
        return productReadRepository.findLowStockProducts(threshold);
    }
}
