package mssu.in.restapi_app.service;

import mssu.in.restapi_app.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductService {
    
    // Basic CRUD operations
    Page<Product> getAllProducts(Pageable pageable);
    
    Optional<Product> getProductById(Long id);
    
    Product createProduct(Product product);
    
    Optional<Product> updateProduct(Long id, Product productDetails);
    
    void deleteProduct(Long id);
    
    // Search and filter operations
    Page<Product> searchProducts(
        String name,
        String description,
        String sku,
        Long categoryId,
        Long manufacturerId,
        Double minPrice,
        Double maxPrice,
        Pageable pageable
    );
    
    // Category and manufacturer specific queries
    Page<Product> getProductsByCategory(Long categoryId, Pageable pageable);
    
    Page<Product> getProductsByManufacturer(Long manufacturerId, Pageable pageable);
    
    Page<Product> getActiveProducts(boolean active, Pageable pageable);
    
    // Specific field updates
    Optional<Product> updateProductStatus(Long id, boolean active);
    
    Optional<Product> updateProductPrice(Long id, BigDecimal price);
    
    Optional<Product> updateProductStock(Long id, int quantity);
}
