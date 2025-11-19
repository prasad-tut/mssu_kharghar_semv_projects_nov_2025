package mssu.in.restapi_app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mssu.in.restapi_app.dto.OrderListResponse;
import mssu.in.restapi_app.entity.Order;
import mssu.in.restapi_app.entity.OrderStatus;
import mssu.in.restapi_app.entity.OrderTimelineType;
import mssu.in.restapi_app.entity.PaymentType;
import mssu.in.restapi_app.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping("/get")
	public List<Order> getAllOrders() {
		return orderService.getAllOrders();
	}
	
	@PostMapping("/add")
	public void addNewOrder(@RequestBody Order order) {
		orderService.addNewOrder(order);
	}
	
	@DeleteMapping("/delete/{id}")
	public void deleteOrder(@PathVariable Integer id) {
		orderService.deleteOrder(id);
	}
	
	@PutMapping("/edit")
	public void editOrder(@RequestBody Order order) {
		orderService.editOrder(order);
	}
	
	@GetMapping("/get/{id}")
	public Order getOrderById(@PathVariable Integer id) {
		return orderService.getOrderById(id);
	}
	
	@GetMapping("/status/{status}")
	public OrderListResponse getOrdersByStatus(@PathVariable OrderStatus status) {
		return orderService.getOrdersByStatus(status);
	}
	
	@GetMapping("/payment/{paymentType}")
	public OrderListResponse getOrdersByPayment(@PathVariable PaymentType paymentType) {
		return orderService.getOrdersByPaymentType(paymentType);
	}
	
	@GetMapping("/timeline")
	public OrderListResponse getOrdersByTimeline(
			@RequestParam OrderTimelineType type,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return orderService.getOrdersByTimeline(type, date);
	}
}

