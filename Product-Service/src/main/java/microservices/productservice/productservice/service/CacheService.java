package microservices.productservice.productservice.service;

import lombok.extern.slf4j.Slf4j;
import microservices.productservice.productservice.readmodel.ProductReadModel;
import microservices.productservice.productservice.readmodel.ProductSummaryReadModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = false)
public class CacheService implements CacheServiceInterface {

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private static final String PRODUCT_SUMMARY_CACHE_PREFIX = "product_summary:";
    private static final String CATEGORY_PRODUCTS_CACHE_PREFIX = "category_products:";
    private static final String BRAND_PRODUCTS_CACHE_PREFIX = "brand_products:";
    private static final String FEATURED_PRODUCTS_CACHE_KEY = "featured_products";
    private static final String HOT_PRODUCTS_CACHE_KEY = "hot_products";
    
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);
    private static final Duration HOT_DATA_TTL = Duration.ofMinutes(5);
    private static final Duration COLD_DATA_TTL = Duration.ofHours(2);
    
    // Product caching
    @Cacheable(value = "products", key = "#productId")
    public ProductReadModel cacheProduct(Long productId, ProductReadModel product) {
        String key = PRODUCT_CACHE_PREFIX + productId;
        redisTemplate.opsForValue().set(key, product, DEFAULT_TTL);
        log.debug("Cached product: {}", productId);
        return product;
    }
    
    public ProductReadModel getProductFromCache(Long productId) {
        String key = PRODUCT_CACHE_PREFIX + productId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof ProductReadModel) {
            log.debug("Cache hit for product: {}", productId);
            return (ProductReadModel) cached;
        }
        log.debug("Cache miss for product: {}", productId);
        return null;
    }
    
    // Product summary caching
    public void cacheProductSummary(Long productId, ProductSummaryReadModel summary) {
        String key = PRODUCT_SUMMARY_CACHE_PREFIX + productId;
        redisTemplate.opsForValue().set(key, summary, DEFAULT_TTL);
        log.debug("Cached product summary: {}", productId);
    }
    
    public ProductSummaryReadModel getProductSummaryFromCache(Long productId) {
        String key = PRODUCT_SUMMARY_CACHE_PREFIX + productId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof ProductSummaryReadModel) {
            log.debug("Cache hit for product summary: {}", productId);
            return (ProductSummaryReadModel) cached;
        }
        log.debug("Cache miss for product summary: {}", productId);
        return null;
    }
    
    // Category products caching
    public void cacheCategoryProducts(Long categoryId, List<ProductSummaryReadModel> products) {
        String key = CATEGORY_PRODUCTS_CACHE_PREFIX + categoryId;
        redisTemplate.opsForValue().set(key, products, DEFAULT_TTL);
        log.debug("Cached category products: {}", categoryId);
    }
    
    @SuppressWarnings("unchecked")
    public List<ProductSummaryReadModel> getCategoryProductsFromCache(Long categoryId) {
        String key = CATEGORY_PRODUCTS_CACHE_PREFIX + categoryId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof List) {
            log.debug("Cache hit for category products: {}", categoryId);
            return (List<ProductSummaryReadModel>) cached;
        }
        log.debug("Cache miss for category products: {}", categoryId);
        return null;
    }
    
    // Brand products caching
    public void cacheBrandProducts(Long brandId, List<ProductSummaryReadModel> products) {
        String key = BRAND_PRODUCTS_CACHE_PREFIX + brandId;
        redisTemplate.opsForValue().set(key, products, DEFAULT_TTL);
        log.debug("Cached brand products: {}", brandId);
    }
    
    @SuppressWarnings("unchecked")
    public List<ProductSummaryReadModel> getBrandProductsFromCache(Long brandId) {
        String key = BRAND_PRODUCTS_CACHE_PREFIX + brandId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof List) {
            log.debug("Cache hit for brand products: {}", brandId);
            return (List<ProductSummaryReadModel>) cached;
        }
        log.debug("Cache miss for brand products: {}", brandId);
        return null;
    }
    
    // Featured products caching (hot data)
    public void cacheFeaturedProducts(List<ProductSummaryReadModel> products) {
        redisTemplate.opsForValue().set(FEATURED_PRODUCTS_CACHE_KEY, products, HOT_DATA_TTL);
        log.debug("Cached featured products");
    }
    
    @SuppressWarnings("unchecked")
    public List<ProductSummaryReadModel> getFeaturedProductsFromCache() {
        Object cached = redisTemplate.opsForValue().get(FEATURED_PRODUCTS_CACHE_KEY);
        if (cached instanceof List) {
            log.debug("Cache hit for featured products");
            return (List<ProductSummaryReadModel>) cached;
        }
        log.debug("Cache miss for featured products");
        return null;
    }
    
    // Hot products caching (frequently accessed)
    public void cacheHotProducts(List<ProductSummaryReadModel> products) {
        redisTemplate.opsForValue().set(HOT_PRODUCTS_CACHE_KEY, products, HOT_DATA_TTL);
        log.debug("Cached hot products");
    }
    
    @SuppressWarnings("unchecked")
    public List<ProductSummaryReadModel> getHotProductsFromCache() {
        Object cached = redisTemplate.opsForValue().get(HOT_PRODUCTS_CACHE_KEY);
        if (cached instanceof List) {
            log.debug("Cache hit for hot products");
            return (List<ProductSummaryReadModel>) cached;
        }
        log.debug("Cache miss for hot products");
        return null;
    }
    
    // Cache eviction methods
    @CacheEvict(value = "products", key = "#productId")
    public void evictProductCache(Long productId) {
        String productKey = PRODUCT_CACHE_PREFIX + productId;
        String summaryKey = PRODUCT_SUMMARY_CACHE_PREFIX + productId;
        
        redisTemplate.delete(productKey);
        redisTemplate.delete(summaryKey);
        
        log.debug("Evicted cache for product: {}", productId);
    }
    
    public void evictCategoryCache(Long categoryId) {
        String key = CATEGORY_PRODUCTS_CACHE_PREFIX + categoryId;
        redisTemplate.delete(key);
        log.debug("Evicted cache for category: {}", categoryId);
    }
    
    public void evictBrandCache(Long brandId) {
        String key = BRAND_PRODUCTS_CACHE_PREFIX + brandId;
        redisTemplate.delete(key);
        log.debug("Evicted cache for brand: {}", brandId);
    }
    
    public void evictFeaturedProductsCache() {
        redisTemplate.delete(FEATURED_PRODUCTS_CACHE_KEY);
        log.debug("Evicted featured products cache");
    }
    
    public void evictHotProductsCache() {
        redisTemplate.delete(HOT_PRODUCTS_CACHE_KEY);
        log.debug("Evicted hot products cache");
    }
    
    // Bulk cache operations
    public void evictAllProductCaches() {
        Set<String> productKeys = redisTemplate.keys(PRODUCT_CACHE_PREFIX + "*");
        Set<String> summaryKeys = redisTemplate.keys(PRODUCT_SUMMARY_CACHE_PREFIX + "*");
        Set<String> categoryKeys = redisTemplate.keys(CATEGORY_PRODUCTS_CACHE_PREFIX + "*");
        Set<String> brandKeys = redisTemplate.keys(BRAND_PRODUCTS_CACHE_PREFIX + "*");
        
        if (productKeys != null && !productKeys.isEmpty()) {
            redisTemplate.delete(productKeys);
        }
        if (summaryKeys != null && !summaryKeys.isEmpty()) {
            redisTemplate.delete(summaryKeys);
        }
        if (categoryKeys != null && !categoryKeys.isEmpty()) {
            redisTemplate.delete(categoryKeys);
        }
        if (brandKeys != null && !brandKeys.isEmpty()) {
            redisTemplate.delete(brandKeys);
        }
        
        redisTemplate.delete(FEATURED_PRODUCTS_CACHE_KEY);
        redisTemplate.delete(HOT_PRODUCTS_CACHE_KEY);
        
        log.info("Evicted all product caches");
    }
    
    // Cache warming
    public void warmUpCache(Long productId, ProductReadModel product) {
        cacheProduct(productId, product);
        log.debug("Warmed up cache for product: {}", productId);
    }
}
