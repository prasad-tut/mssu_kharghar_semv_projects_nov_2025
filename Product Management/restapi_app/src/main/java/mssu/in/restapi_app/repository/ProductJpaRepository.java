package mssu.in.restapi_app.repository;

import mssu.in.restapi_app.entity.Product;
import mssu.in.restapi_app.entity.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    
    // Custom query methods
    Page<Product> findByActive(boolean active, Pageable pageable);
    
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    Page<Product> findByManufacturerId(Long manufacturerId, Pageable pageable);
    
    // Search products with multiple criteria
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:description IS NULL OR LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
           "(:sku IS NULL OR p.sku = :sku) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:manufacturerId IS NULL OR p.manufacturer.id = :manufacturerId) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchProducts(
        @Param("name") String name,
        @Param("description") String description,
        @Param("sku") String sku,
        @Param("categoryId") Long categoryId,
        @Param("manufacturerId") Long manufacturerId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );
    
    // Find by SKU (unique identifier for products)
    Optional<Product> findBySku(String sku);
    
    // Check if a product with the given SKU exists
    boolean existsBySku(String sku);
    
    // Find products by name containing (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Find products with low stock (quantity less than or equal to the specified value)
    List<Product> findByQuantityLessThanEqual(int quantity);
    
    // Find products by price range
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Find products by product type
    List<Product> findByProductType(ProductType productType);
    
    // Find products that are expired (expiry date before current date)
    @Query("SELECT p FROM Product p WHERE p.expiryDate < CURRENT_DATE")
    List<Product> findExpiredProducts();
}
