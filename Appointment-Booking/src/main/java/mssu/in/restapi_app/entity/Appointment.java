package mssu.in.restapi_app.entity;

public class Appointment {
	private Integer appointmentId;
	private Integer clientId;
	private Integer providerId;
	private Integer serviceId;
	private String appointmentDate;
	private String appointmentTime;
	private String status;
	private String createdAt;
	
	public Appointment() {}
	
	public Appointment(Integer appointmentId, Integer clientId, Integer providerId, Integer serviceId, 
			String appointmentDate, String appointmentTime, String status, String createdAt) {
		super();
		this.appointmentId = appointmentId;
		this.clientId = clientId;
		this.providerId = providerId;
		this.serviceId = serviceId;
		this.appointmentDate = appointmentDate;
		this.appointmentTime = appointmentTime;
		this.status = status;
		this.createdAt = createdAt;
	}
	
	public Integer getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(Integer appointmentId) {
		this.appointmentId = appointmentId;
	}
	public Integer getClientId() {
		return clientId;
	}
	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}
	public Integer getProviderId() {
		return providerId;
	}
	public void setProviderId(Integer providerId) {
		this.providerId = providerId;
	}
	public Integer getServiceId() {
		return serviceId;
	}
	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}
	public String getAppointmentDate() {
		return appointmentDate;
	}
	public void setAppointmentDate(String appointmentDate) {
		this.appointmentDate = appointmentDate;
	}
	public String getAppointmentTime() {
		return appointmentTime;
	}
	public void setAppointmentTime(String appointmentTime) {
		this.appointmentTime = appointmentTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	
	@Override
	public String toString() {
		return "Appointment [appointmentId=" + appointmentId + ", clientId=" + clientId + ", providerId=" + providerId
				+ ", serviceId=" + serviceId + ", appointmentDate=" + appointmentDate + ", appointmentTime="
				+ appointmentTime + ", status=" + status + ", createdAt=" + createdAt + "]";
	}
}
