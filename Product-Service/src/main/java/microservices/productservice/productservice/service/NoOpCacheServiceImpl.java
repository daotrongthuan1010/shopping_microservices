package microservices.productservice.productservice.service;

import lombok.extern.slf4j.Slf4j;
import microservices.productservice.productservice.readmodel.ProductReadModel;
import microservices.productservice.productservice.readmodel.ProductSummaryReadModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "none")
@Slf4j
public class NoOpCacheServiceImpl implements CacheServiceInterface {

    public NoOpCacheServiceImpl() {
        log.info("Using No-Op Cache Service - caching is disabled");
    }

    @Override
    public ProductReadModel cacheProduct(Long productId, ProductReadModel product) {
        log.debug("No-op: Would cache product: {}", productId);
        return product;
    }

    @Override
    public ProductReadModel getProductFromCache(Long productId) {
        log.debug("No-op: Would get product from cache: {}", productId);
        return null; // Always cache miss
    }

    @Override
    public void cacheProductSummary(Long productId, ProductSummaryReadModel summary) {
        log.debug("No-op: Would cache product summary: {}", productId);
    }

    @Override
    public ProductSummaryReadModel getProductSummaryFromCache(Long productId) {
        log.debug("No-op: Would get product summary from cache: {}", productId);
        return null; // Always cache miss
    }

    @Override
    public void cacheCategoryProducts(Long categoryId, List<ProductSummaryReadModel> products) {
        log.debug("No-op: Would cache category products: {}", categoryId);
    }

    @Override
    public List<ProductSummaryReadModel> getCategoryProductsFromCache(Long categoryId) {
        log.debug("No-op: Would get category products from cache: {}", categoryId);
        return null; // Always cache miss
    }

    @Override
    public void cacheBrandProducts(Long brandId, List<ProductSummaryReadModel> products) {
        log.debug("No-op: Would cache brand products: {}", brandId);
    }

    @Override
    public List<ProductSummaryReadModel> getBrandProductsFromCache(Long brandId) {
        log.debug("No-op: Would get brand products from cache: {}", brandId);
        return null; // Always cache miss
    }

    @Override
    public void cacheFeaturedProducts(List<ProductSummaryReadModel> products) {
        log.debug("No-op: Would cache featured products");
    }

    @Override
    public List<ProductSummaryReadModel> getFeaturedProductsFromCache() {
        log.debug("No-op: Would get featured products from cache");
        return null; // Always cache miss
    }

    @Override
    public void cacheHotProducts(List<ProductSummaryReadModel> products) {
        log.debug("No-op: Would cache hot products");
    }

    @Override
    public List<ProductSummaryReadModel> getHotProductsFromCache() {
        log.debug("No-op: Would get hot products from cache");
        return null; // Always cache miss
    }

    @Override
    public void evictProductCache(Long productId) {
        log.debug("No-op: Would evict product cache: {}", productId);
    }

    @Override
    public void evictCategoryCache(Long categoryId) {
        log.debug("No-op: Would evict category cache: {}", categoryId);
    }

    @Override
    public void evictBrandCache(Long brandId) {
        log.debug("No-op: Would evict brand cache: {}", brandId);
    }

    @Override
    public void evictFeaturedProductsCache() {
        log.debug("No-op: Would evict featured products cache");
    }

    @Override
    public void evictHotProductsCache() {
        log.debug("No-op: Would evict hot products cache");
    }

    @Override
    public void evictAllProductCaches() {
        log.debug("No-op: Would evict all product caches");
    }

    @Override
    public void warmUpCache(Long productId, ProductReadModel product) {
        log.debug("No-op: Would warm up cache for product: {}", productId);
    }
}
