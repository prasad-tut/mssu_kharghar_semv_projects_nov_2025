package mssu.in.restapi_app.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mssu.in.restapi_app.dto.OrderListResponse;
import mssu.in.restapi_app.entity.Order;
import mssu.in.restapi_app.entity.OrderTimelineType;
import mssu.in.restapi_app.entity.OrderStatus;
import mssu.in.restapi_app.entity.PaymentType;
import mssu.in.restapi_app.repository.OrderRepository;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	public List<Order> getAllOrders() {
		return orderRepository.getAllOrders();
	}
	
	public void addNewOrder(Order order) {
		orderRepository.addNewOrder(order);
	}
	
	public void deleteOrder(Integer id) {
		orderRepository.deleteOrder(id);
	}
	
	public void editOrder(Order order) {
		if (order == null || order.getId() == null) {
			throw new IllegalArgumentException("Order id is required for update.");
		}
		
		Order existing = orderRepository.getOrderById(order.getId());
		Order merged = mergeOrders(existing, order);
		orderRepository.editOrder(merged);
	}
	
	public Order getOrderById(Integer id) {
		return orderRepository.getOrderById(id);
	}
	
	public OrderListResponse getOrdersByStatus(OrderStatus status) {
		return new OrderListResponse(orderRepository.getOrdersByStatus(status));
	}
	
	public OrderListResponse getOrdersByPaymentType(PaymentType paymentType) {
		return new OrderListResponse(orderRepository.getOrdersByPaymentType(paymentType));
	}
	
	public OrderListResponse getOrdersByTimeline(OrderTimelineType type, LocalDate date) {
		List<Order> orders;
		switch (type) {
		case SHIPPED:
			orders = orderRepository.getOrdersByShippingDate(date);
			break;
		case DELIVERED:
			orders = orderRepository.getOrdersByDeliveryDate(date);
			break;
		case CREATED:
		default:
			orders = orderRepository.getOrdersByOrderDate(date);
			break;
		}
		return new OrderListResponse(orders);
	}
	
	private Order mergeOrders(Order base, Order updates) {
		if (updates.getCustomerName() != null) {
			base.setCustomerName(updates.getCustomerName());
		}
		if (updates.getCustomerContact() != null) {
			base.setCustomerContact(updates.getCustomerContact());
		}
		if (updates.getDescription() != null) {
			base.setDescription(updates.getDescription());
		}
		if (updates.getOrderDate() != null) {
			base.setOrderDate(updates.getOrderDate());
		}
		if (updates.getShippingDate() != null) {
			base.setShippingDate(updates.getShippingDate());
		}
		if (updates.getDeliveryDate() != null) {
			base.setDeliveryDate(updates.getDeliveryDate());
		}
		if (updates.getStatus() != null) {
			base.setStatus(updates.getStatus());
		}
		if (updates.getDeliveryAddress() != null) {
			base.setDeliveryAddress(updates.getDeliveryAddress());
		}
		if (updates.getQuantity() != null) {
			base.setQuantity(updates.getQuantity());
		}
		if (updates.getTotalPrice() != null) {
			base.setTotalPrice(updates.getTotalPrice());
		}
		if (updates.getPaymentType() != null) {
			base.setPaymentType(updates.getPaymentType());
		}
		if (updates.getOrderNotes() != null) {
			base.setOrderNotes(updates.getOrderNotes());
		}
		if (updates.getCreatedAt() != null) {
			base.setCreatedAt(updates.getCreatedAt());
		}
		return base;
	}
}

