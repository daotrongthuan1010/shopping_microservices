package microservices.productservice.productservice.service;

import microservices.productservice.productservice.readmodel.ProductReadModel;
import microservices.productservice.productservice.readmodel.ProductSummaryReadModel;

import java.util.List;

public interface CacheServiceInterface {
    
    ProductReadModel cacheProduct(Long productId, ProductReadModel product);
    ProductReadModel getProductFromCache(Long productId);
    
    void cacheProductSummary(Long productId, ProductSummaryReadModel summary);
    ProductSummaryReadModel getProductSummaryFromCache(Long productId);
    
    void cacheCategoryProducts(Long categoryId, List<ProductSummaryReadModel> products);
    List<ProductSummaryReadModel> getCategoryProductsFromCache(Long categoryId);
    
    void cacheBrandProducts(Long brandId, List<ProductSummaryReadModel> products);
    List<ProductSummaryReadModel> getBrandProductsFromCache(Long brandId);
    
    void cacheFeaturedProducts(List<ProductSummaryReadModel> products);
    List<ProductSummaryReadModel> getFeaturedProductsFromCache();
    
    void cacheHotProducts(List<ProductSummaryReadModel> products);
    List<ProductSummaryReadModel> getHotProductsFromCache();
    
    void evictProductCache(Long productId);
    void evictCategoryCache(Long categoryId);
    void evictBrandCache(Long brandId);
    void evictFeaturedProductsCache();
    void evictHotProductsCache();
    void evictAllProductCaches();
    
    void warmUpCache(Long productId, ProductReadModel product);
}
