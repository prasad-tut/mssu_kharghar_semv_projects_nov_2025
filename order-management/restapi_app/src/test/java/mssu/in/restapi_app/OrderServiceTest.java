package mssu.in.restapi_app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mssu.in.restapi_app.entity.Order;
import mssu.in.restapi_app.entity.OrderStatus;
import mssu.in.restapi_app.entity.PaymentType;
import mssu.in.restapi_app.repository.OrderRepository;
import mssu.in.restapi_app.service.OrderService;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private OrderService orderService;

	@Test
	void editOrderShouldMergeNonNullFieldsOnly() {
		Order existing = buildOrder(5, "Alice", "Initial description", OrderStatus.PENDING, 2, PaymentType.COD);
		when(orderRepository.getOrderById(5)).thenReturn(existing);

		Order updates = new Order();
		updates.setId(5);
		updates.setStatus(OrderStatus.DISPATCHED);
		updates.setQuantity(10);

		orderService.editOrder(updates);

		ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
		verify(orderRepository).editOrder(captor.capture());
		Order merged = captor.getValue();

		verify(orderRepository).getOrderById(5);
		// unchanged fields remain
		assertThat(merged.getCustomerName()).isEqualTo("Alice");
		assertThat(merged.getDescription()).isEqualTo("Initial description");
		// updated fields changed
		assertThat(merged.getStatus()).isEqualTo(OrderStatus.DISPATCHED);
		assertThat(merged.getQuantity()).isEqualTo(10);
	}

	@Test
	void editOrderRequiresId() {
		assertThatThrownBy(() -> orderService.editOrder(new Order()))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Order id is required");
	}

	private static Order buildOrder(int id, String customerName, String description, OrderStatus status, int quantity,
			PaymentType paymentType) {
		Order order = new Order();
		order.setId(id);
		order.setCustomerName(customerName);
		order.setCustomerContact("123456789");
		order.setDescription(description);
		order.setOrderDate(LocalDate.now());
		order.setStatus(status);
		order.setDeliveryAddress("Address");
		order.setQuantity(quantity);
		order.setTotalPrice(new BigDecimal("100.00"));
		order.setPaymentType(paymentType);
		order.setOrderNotes("note");
		return order;
	}
}

