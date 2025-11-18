package mssu.in.restapi_app.repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import mssu.in.restapi_app.entity.Appointment;

@Repository
public class AppointmentRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Appointment> getAllAppointments() {
		String sql = "SELECT * FROM appointments";   
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			Appointment appointment = new Appointment();
			appointment.setAppointmentId(rs.getInt("appointment_id"));
			appointment.setClientId(rs.getInt("client_id"));
			appointment.setProviderId(rs.getInt("provider_id"));
			appointment.setServiceId(rs.getInt("service_id"));
			appointment.setAppointmentDate(rs.getString("appointment_date"));
			appointment.setAppointmentTime(rs.getString("appointment_time"));
			appointment.setStatus(rs.getString("status"));
			appointment.setCreatedAt(rs.getString("created_at"));
			return appointment;
		});
	}
	
	public void addNewAppointment(Appointment appointment) {
		String sql = "INSERT INTO appointments(client_id, provider_id, service_id, appointment_date, appointment_time, status, created_at) VALUES (?,?,?,?,?,?,?)";
		jdbcTemplate.update(
			sql,
			appointment.getClientId(),
			appointment.getProviderId(),
			appointment.getServiceId(),
			appointment.getAppointmentDate(),
			appointment.getAppointmentTime(),
			appointment.getStatus(),
			appointment.getCreatedAt()
		);
	}
	
	public void editAppointment(Appointment appointment) {
		String sql = "UPDATE appointments SET client_id=?, provider_id=?, service_id=?, appointment_date=?, appointment_time=?, status=?, created_at=? WHERE appointment_id=?";
		jdbcTemplate.update(
			sql,
			appointment.getClientId(),
			appointment.getProviderId(),
			appointment.getServiceId(),
			appointment.getAppointmentDate(),
			appointment.getAppointmentTime(),
			appointment.getStatus(),
			appointment.getCreatedAt(),
			appointment.getAppointmentId()
		);
	}
	
	public void deleteAppointment(Integer id) {
		String sql = "DELETE FROM appointments WHERE appointment_id=?";
		jdbcTemplate.update(sql, id);
	}
	
	public Appointment getAppointmentById(Integer id) {
		String sql = "SELECT * FROM appointments WHERE appointment_id = ?";
		return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
			Appointment appointment = new Appointment();
			appointment.setAppointmentId(rs.getInt("appointment_id"));
			appointment.setClientId(rs.getInt("client_id"));
			appointment.setProviderId(rs.getInt("provider_id"));
			appointment.setServiceId(rs.getInt("service_id"));
			appointment.setAppointmentDate(rs.getString("appointment_date"));
			appointment.setAppointmentTime(rs.getString("appointment_time"));
			appointment.setStatus(rs.getString("status"));
			appointment.setCreatedAt(rs.getString("created_at"));
			return appointment;
		}, id);
	}
}
