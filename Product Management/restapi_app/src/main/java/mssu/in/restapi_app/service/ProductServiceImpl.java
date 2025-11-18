package mssu.in.restapi_app.service;

import mssu.in.restapi_app.entity.Product;
import mssu.in.restapi_app.exception.ResourceNotFoundException;
import mssu.in.restapi_app.repository.ProductJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductJpaRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductJpaRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product createProduct(Product product) {
        // Set timestamps
        product.setDateCreated(java.time.LocalDateTime.now());
        product.setDateModified(java.time.LocalDateTime.now());
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id).map(product -> {
            // Update fields
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setSku(productDetails.getSku());
            product.setPrice(productDetails.getPrice());
            product.setTax(productDetails.getTax());
            product.setQuantity(productDetails.getQuantity());
            product.setActive(productDetails.isActive());
            product.setManufacturer(productDetails.getManufacturer());
            product.setCategory(productDetails.getCategory());
            product.setProductType(productDetails.getProductType());
            product.setModel(productDetails.getModel());
            product.setUpc(productDetails.getUpc());
            product.setEan(productDetails.getEan());
            product.setIsbn(productDetails.getIsbn());
            product.setImage(productDetails.getImage());
            product.setManufacturedOn(productDetails.getManufacturedOn());
            product.setExpiryDate(productDetails.getExpiryDate());
            
            // Update the modified timestamp
            product.setDateModified(java.time.LocalDateTime.now());
            
            return productRepository.save(product);
        });
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<Product> searchProducts(
            String name,
            String description,
            String sku,
            Long categoryId,
            Long manufacturerId,
            Double minPrice,
            Double maxPrice,
            Pageable pageable) {
        
        return productRepository.searchProducts(
            name, 
            description, 
            sku, 
            categoryId, 
            manufacturerId, 
            minPrice != null ? BigDecimal.valueOf(minPrice) : null,
            maxPrice != null ? BigDecimal.valueOf(maxPrice) : null,
            pageable
        );
    }

    @Override
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    @Override
    public Page<Product> getProductsByManufacturer(Long manufacturerId, Pageable pageable) {
        return productRepository.findByManufacturerId(manufacturerId, pageable);
    }

    @Override
    public Page<Product> getActiveProducts(boolean active, Pageable pageable) {
        return productRepository.findByActive(active, pageable);
    }

    @Override
    public Optional<Product> updateProductStatus(Long id, boolean active) {
        return productRepository.findById(id).map(product -> {
            product.setActive(active);
            product.setDateModified(java.time.LocalDateTime.now());
            return productRepository.save(product);
        });
    }

    @Override
    public Optional<Product> updateProductPrice(Long id, BigDecimal price) {
        return productRepository.findById(id).map(product -> {
            product.setPrice(price);
            product.setDateModified(java.time.LocalDateTime.now());
            return productRepository.save(product);
        });
    }

    @Override
    public Optional<Product> updateProductStock(Long id, int quantity) {
        return productRepository.findById(id).map(product -> {
            product.setQuantity(quantity);
            product.setDateModified(java.time.LocalDateTime.now());
            return productRepository.save(product);
        });
    }
}
