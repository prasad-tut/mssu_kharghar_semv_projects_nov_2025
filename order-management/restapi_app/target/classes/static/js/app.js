(() => {
	const API_BASE = "/orders";

	const qs = (sel, scope = document) => scope.querySelector(sel);
	const qsa = (sel, scope = document) => Array.from(scope.querySelectorAll(sel));

	const sidebarItems = qsa(".sidebar li");
	const sections = qsa(".panel");
	const refreshBtn = qs("#refreshAll");

	const tableContainer = qs("#ordersTableContainer");
	const getOrderResult = qs("#getOrderResult");
	const createOrderResult = qs("#createOrderResult");
	const updateOrderResult = qs("#updateOrderResult");
	const deleteOrderResult = qs("#deleteOrderResult");
	const statusOrderResult = qs("#statusOrderResult");
	const statusFilterSummary = qs("#statusFilterSummary");
	const paymentFilterSummary = qs("#paymentFilterSummary");
	const dateFilterSummary = qs("#dateFilterSummary");
	const statusFilterTable = qs("#statusFilterTable");
	const paymentFilterTable = qs("#paymentFilterTable");
	const dateFilterTable = qs("#dateFilterTable");

	const formGetOrder = qs("#formGetOrder");
	const formCreateOrder = qs("#formCreateOrder");
	const formUpdateOrder = qs("#formUpdateOrder");
	const formDeleteOrder = qs("#formDeleteOrder");
	const formStatusOrder = qs("#formStatusOrder");
	const formFilterStatus = qs("#formFilterStatus");
	const formFilterPayment = qs("#formFilterPayment");
	const formFilterDate = qs("#formFilterDate");

	sidebarItems.forEach((item) => {
		item.addEventListener("click", () => {
			sidebarItems.forEach((i) => i.classList.remove("active"));
			item.classList.add("active");

			const targetId = item.dataset.target;
			sections.forEach((section) =>
				section.classList.toggle("visible", section.id === targetId)
			);
		});
	});

	refreshBtn.addEventListener("click", async () => {
		await loadAllOrders();
	});

	formGetOrder.addEventListener("submit", async (event) => {
		event.preventDefault();
		const id = event.target.orderId.value.trim();
		if (!id) return;
		const res = await apiRequest(`${API_BASE}/get/${id}`, { method: "GET" });
		renderCard(res, getOrderResult);
	});

	formCreateOrder.addEventListener("submit", async (event) => {
		event.preventDefault();
		const payload = formToPayload(event.target, false);
		await apiRequest(`${API_BASE}/add`, {
			method: "POST",
			body: JSON.stringify(payload),
		});
		showToast("Order created", createOrderResult, true);
		event.target.reset();
		await loadAllOrders();
	});

	formUpdateOrder.addEventListener("submit", async (event) => {
		event.preventDefault();
		const payload = formToPayload(event.target, true);
		await apiRequest(`${API_BASE}/edit`, {
			method: "PUT",
			body: JSON.stringify(payload),
		});
		showToast("Order updated", updateOrderResult, true);
		await loadAllOrders();
	});

	formDeleteOrder.addEventListener("submit", async (event) => {
		event.preventDefault();
		const id = event.target.orderId.value.trim();
		if (!id) return;
		await apiRequest(`${API_BASE}/delete/${id}`, { method: "DELETE" });
		showToast(`Order ${id} deleted`, deleteOrderResult, true);
		event.target.reset();
		await loadAllOrders();
	});

	formStatusOrder.addEventListener("submit", async (event) => {
		event.preventDefault();
		const id = event.target.orderId.value.trim();
		if (!id) return;
		const order = await apiRequest(`${API_BASE}/get/${id}`, {
			method: "GET",
		});
		if (order && order.status) {
			statusOrderResult.innerHTML = `<div class="toast success">Status: ${order.status}</div>`;
		} else {
			statusOrderResult.innerHTML =
				'<div class="toast error">No status available.</div>';
		}
	});

	formFilterStatus?.addEventListener("submit", async (event) => {
		event.preventDefault();
		const status = event.target.status.value;
		await handleFilterRequest(
			`${API_BASE}/status/${status}`,
			`${status} orders`,
			statusFilterSummary,
			statusFilterTable
		);
	});

	formFilterPayment?.addEventListener("submit", async (event) => {
		event.preventDefault();
		const paymentType = event.target.paymentType.value;
		await handleFilterRequest(
			`${API_BASE}/payment/${paymentType}`,
			`${paymentType} payments`,
			paymentFilterSummary,
			paymentFilterTable
		);
	});

	formFilterDate?.addEventListener("submit", async (event) => {
		event.preventDefault();
		const type = event.target.timelineType.value;
		const date = event.target.timelineDate.value;
		if (!date) return;
		const label = `${type.toLowerCase()} on ${date}`;
		await handleFilterRequest(
			`${API_BASE}/timeline?type=${type}&date=${date}`,
			label,
			dateFilterSummary,
			dateFilterTable
		);
	});

	async function loadAllOrders() {
		try {
			const orders = await apiRequest(`${API_BASE}/get`, { method: "GET" });
			renderOrdersTable(orders);
		} catch (error) {
			console.error(error);
			tableContainer.innerHTML =
				'<div class="toast error">Failed to load orders.</div>';
		}
	}

	async function handleFilterRequest(url, label, summaryTarget, tableTarget) {
		try {
			const data = await apiRequest(url, { method: "GET" });
			if (summaryTarget) {
				renderFilterSummary(summaryTarget, label, data.count);
			}
			renderOrdersTable(data.orders, tableTarget);
		} catch (error) {
			console.error(error);
			if (summaryTarget) {
				summaryTarget.innerHTML =
					'<div class="toast error">Failed to load data.</div>';
			}
			renderOrdersTable([], tableTarget);
		}
	}

	function renderFilterSummary(target, label, count) {
		target.innerHTML = `<div class="toast success">${label}: <strong>${count}</strong></div>`;
	}

	function renderOrdersTable(orders, target = tableContainer) {
		if (!target) return;
		if (!orders || !orders.length) {
			target.classList.add("empty");
			target.innerHTML = "<p>No orders available.</p>";
			return;
		}
		target.classList.remove("empty");
		const headers = [
			"ID",
			"Customer",
			"Contact",
			"Description",
			"Status",
			"Quantity",
			"Total Price",
			"Payment",
			"Created",
		];
		const rows = orders
			.map(
				(o) => `
				<tr>
					<td>${safe(o.id)}</td>
					<td>${safe(o.customerName)}</td>
					<td>${safe(o.customerContact)}</td>
					<td>${safe(o.description)}</td>
					<td>${safe(o.status)}</td>
					<td>${safe(o.quantity)}</td>
					<td>${safe(o.totalPrice)}</td>
					<td>${safe(o.paymentType)}</td>
					<td>${formatDateTime(o.createdAt)}</td>
				</tr>`
			)
			.join("");

		target.innerHTML = `
			<table>
				<thead>
					<tr>${headers.map((h) => `<th>${h}</th>`).join("")}</tr>
				</thead>
				<tbody>${rows}</tbody>
			</table>`;
	}

	function renderCard(order, target) {
		if (!order) {
			target.innerHTML = '<div class="toast error">Order not found.</div>';
			return;
		}
		target.innerHTML = `
			<div class="card">
				<div><strong>ID</strong>${safe(order.id)}</div>
				<div><strong>Customer</strong>${safe(order.customerName)}</div>
				<div><strong>Status</strong>${safe(order.status)}</div>
				<div><strong>Quantity</strong>${safe(order.quantity)}</div>
				<div><strong>Total</strong>${safe(order.totalPrice)}</div>
				<div><strong>Payment</strong>${safe(order.paymentType)}</div>
				<div><strong>Ordered</strong>${safe(order.orderDate)}</div>
				<div><strong>Delivery</strong>${safe(order.deliveryDate)}</div>
				<div><strong>Notes</strong>${safe(order.orderNotes)}</div>
			</div>
		`;
	}

	function showToast(message, target, success = true) {
		target.innerHTML = `<span class="toast ${
			success ? "success" : "error"
		}">${message}</span>`;
	}

	async function apiRequest(url, options) {
		const config = {
			headers: { "Content-Type": "application/json" },
			...options,
		};
		const response = await fetch(url, config);
		if (!response.ok) {
			const text = await response.text();
			throw new Error(text || `Request failed: ${response.status}`);
		}
		if (response.status === 204) return null;
		const contentType = response.headers.get("content-type") || "";
		if (contentType.includes("application/json")) {
			return response.json();
		}
		return response.text();
	}

	function formToPayload(form, includeId) {
		const data = Object.fromEntries(new FormData(form).entries());

		const payload = {
			customerName: valueOrNull(data.customerName),
			customerContact: valueOrNull(data.customerContact),
			description: valueOrNull(data.description),
			orderDate: valueOrNull(data.orderDate),
			shippingDate: valueOrNull(data.shippingDate),
			deliveryDate: valueOrNull(data.deliveryDate),
			status: valueOrNull(data.status),
			deliveryAddress: valueOrNull(data.deliveryAddress),
			quantity: valueOrNumber(data.quantity),
			totalPrice: valueOrNumber(data.totalPrice, true),
			paymentType: valueOrNull(data.paymentType),
			orderNotes: valueOrNull(data.orderNotes),
		};

		if (includeId) {
			payload.id = Number(data.id);
		}

		return payload;
	}

	function valueOrNull(value) {
		return value && value.trim().length ? value.trim() : null;
	}

	function valueOrNumber(value, isDecimal = false) {
		if (!value || !value.trim().length) return null;
		return isDecimal ? Number.parseFloat(value) : Number.parseInt(value, 10);
	}

	function safe(value) {
		if (value === null || value === undefined) return "-";
		return String(value);
	}

	function formatDateTime(value) {
		if (!value) return "-";
		try {
			return new Date(value).toLocaleString();
		} catch (e) {
			return value;
		}
	}

	// auto-load orders initially
	loadAllOrders();
})();

