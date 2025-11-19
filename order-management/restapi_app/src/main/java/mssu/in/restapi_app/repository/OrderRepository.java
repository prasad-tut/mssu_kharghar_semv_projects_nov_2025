package mssu.in.restapi_app.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import mssu.in.restapi_app.entity.Order;
import mssu.in.restapi_app.entity.OrderStatus;
import mssu.in.restapi_app.entity.PaymentType;

@Repository
public class OrderRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Order> getAllOrders() {
		String sql = "select * from orders";
		return jdbcTemplate.query(sql, new OrderRowMapper());
	}
	
	public void addNewOrder(Order order) {
		String sql = """
				insert into orders
				(customer_name, customer_contact, description, order_date, shipping_date, delivery_date, status,
				delivery_address, quantity, total_price, payment_type, order_notes)
				values (?,?,?,?,?,?,?,?,?,?,?,?)
				""";
		jdbcTemplate.update(
				sql,
				order.getCustomerName(),
				order.getCustomerContact(),
				order.getDescription(),
				order.getOrderDate(),
				order.getShippingDate(),
				order.getDeliveryDate(),
				toDbStatus(order.getStatus()),
				order.getDeliveryAddress(),
				order.getQuantity(),
				order.getTotalPrice(),
				toDbPaymentType(order.getPaymentType()),
				order.getOrderNotes()
		);
	}
	
	public void editOrder(Order order) {
		String sql = """
				update orders set customer_name=?, customer_contact=?, description=?, order_date=?, shipping_date=?,
				delivery_date=?, status=?, delivery_address=?, quantity=?, total_price=?, payment_type=?, order_notes=?
				where id=?
				""";
		jdbcTemplate.update(
				sql,
				order.getCustomerName(),
				order.getCustomerContact(),
				order.getDescription(),
				order.getOrderDate(),
				order.getShippingDate(),
				order.getDeliveryDate(),
				toDbStatus(order.getStatus()),
				order.getDeliveryAddress(),
				order.getQuantity(),
				order.getTotalPrice(),
				toDbPaymentType(order.getPaymentType()),
				order.getOrderNotes(),
				order.getId()
		);
	}
	
	public void deleteOrder(Integer id) {
		String sql = "delete from orders where id=?";
		jdbcTemplate.update(sql, id);
	}
	
	public Order getOrderById(Integer id) {
		String sql = "select * from orders where id = ?";
		return jdbcTemplate.queryForObject(sql, new OrderRowMapper(), id);
	}
	
	public List<Order> getOrdersByStatus(OrderStatus status) {
		String sql = "select * from orders where lower(status) = ?";
		return jdbcTemplate.query(sql, new OrderRowMapper(), toDbStatus(status));
	}
	
	public List<Order> getOrdersByPaymentType(PaymentType paymentType) {
		String sql = "select * from orders where lower(payment_type) = ?";
		return jdbcTemplate.query(sql, new OrderRowMapper(), toDbPaymentType(paymentType).toLowerCase());
	}
	
	public List<Order> getOrdersByOrderDate(LocalDate date) {
		String sql = "select * from orders where order_date = ?";
		return jdbcTemplate.query(sql, new OrderRowMapper(), date);
	}
	
	public List<Order> getOrdersByShippingDate(LocalDate date) {
		String sql = "select * from orders where shipping_date = ?";
		return jdbcTemplate.query(sql, new OrderRowMapper(), date);
	}
	
	public List<Order> getOrdersByDeliveryDate(LocalDate date) {
		String sql = "select * from orders where delivery_date = ?";
		return jdbcTemplate.query(sql, new OrderRowMapper(), date);
	}
	
	private static String toDbStatus(OrderStatus status) {
		return status == null ? "pending" : status.name().toLowerCase();
	}
	
	private static String toDbPaymentType(PaymentType paymentType) {
		if (paymentType == null) {
			return "COD";
		}
		switch (paymentType) {
		case PAID:
			return "paid";
		case COD:
		default:
			return "COD";
		}
	}
	
	private static class OrderRowMapper implements RowMapper<Order> {
		@Override
		public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
			Order order = new Order();
			order.setId(rs.getInt("id"));
			order.setCustomerName(rs.getString("customer_name"));
			order.setCustomerContact(rs.getString("customer_contact"));
			order.setDescription(rs.getString("description"));
			order.setOrderDate(toLocalDate(rs.getDate("order_date")));
			order.setShippingDate(toLocalDate(rs.getDate("shipping_date")));
			order.setDeliveryDate(toLocalDate(rs.getDate("delivery_date")));
			order.setStatus(toStatus(rs.getString("status")));
			order.setDeliveryAddress(rs.getString("delivery_address"));
			int quantity = rs.getInt("quantity");
			order.setQuantity(rs.wasNull() ? null : quantity);
			order.setTotalPrice(rs.getBigDecimal("total_price"));
			order.setPaymentType(toPaymentType(rs.getString("payment_type")));
			order.setOrderNotes(rs.getString("order_notes"));
			order.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
			return order;
		}
		
		private static java.time.LocalDate toLocalDate(Date date) {
			return date == null ? null : date.toLocalDate();
		}
		
		private static java.time.LocalDateTime toLocalDateTime(Timestamp timestamp) {
			return timestamp == null ? null : timestamp.toLocalDateTime();
		}
		
		private static OrderStatus toStatus(String value) {
			return value == null ? null : OrderStatus.valueOf(value.toUpperCase());
		}
		
		private static PaymentType toPaymentType(String value) {
			return value == null ? null : PaymentType.valueOf(value.toUpperCase());
		}
	}
}

