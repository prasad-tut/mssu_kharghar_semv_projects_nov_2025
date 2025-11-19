package mssu.in.restapi_app.dto;

import java.util.List;

import mssu.in.restapi_app.entity.Order;

public class OrderListResponse {

	private final long count;
	private final List<Order> orders;

	public OrderListResponse(List<Order> orders) {
		this.orders = orders;
		this.count = orders == null ? 0 : orders.size();
	}

	public long getCount() {
		return count;
	}

	public List<Order> getOrders() {
		return orders;
	}
}

