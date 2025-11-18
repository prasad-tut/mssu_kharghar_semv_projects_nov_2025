package mssu.in.restapi_app.repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import mssu.in.restapi_app.entity.Product;

@Repository
public class ProductRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Product> getAllProducts() {
		String sql = "SELECT * FROM product";   
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class));
	}
	
	public void addNewProduct(Product product) {
		String sql = "INSERT INTO product(description, manufacturer, productType, manufacturedOn, expiryDate, unitPrice, tax) VALUES (?,?,?,?,?,?,?)";
		jdbcTemplate.update(
			sql,
			product.getDescription(),
			product.getManufacturer(),
			product.getProductType().name(),
			product.getManufacturedOn(),
			product.getExpiryDate(),
			product.getUnitPrice(),
			product.getTax()
		);
	}
	
	public void editProduct(Product product) {
		String sql = "UPDATE product SET description=?, manufacturer=?, productType=?, manufacturedOn=?, expiryDate=?, unitPrice=?, tax=? WHERE id=?";
		jdbcTemplate.update(
			sql,
			product.getDescription(),
			product.getManufacturer(),
			product.getProductType().name(),
			product.getManufacturedOn(),
			product.getExpiryDate(),
			product.getUnitPrice(),
			product.getTax(),
			product.getId()
		);
	}
	
	public void deleteProduct(Integer id) {
		String sql = "DELETE FROM product WHERE id=?";
		jdbcTemplate.update(sql, id);
	}
	
	public Product getProductById(Integer id) {
		String sql = "SELECT * FROM product WHERE id = ?";
		return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Product.class), id);
	}

}
