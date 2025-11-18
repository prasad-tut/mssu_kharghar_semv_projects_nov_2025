package mssu.in.restapi_app.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mssu.in.restapi_app.entity.Appointment;
import mssu.in.restapi_app.repository.AppointmentRepository;

@Service
public class AppointmentService {
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	public List<Appointment> getAllAppointments(){
		return appointmentRepository.getAllAppointments();
	}
	
	public void addNewAppointment(Appointment appointment) {
		appointmentRepository.addNewAppointment(appointment);
	}
	
	public void deleteAppointment(Integer id){
		appointmentRepository.deleteAppointment(id);
	}
	
	public void editAppointment(Appointment appointment) {
		appointmentRepository.editAppointment(appointment);
	}
	
	public Appointment getAppointmentById(Integer id) {
		return appointmentRepository.getAppointmentById(id);
	}
}
