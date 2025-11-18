package mssu.in.restapi_app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mssu.in.restapi_app.entity.Appointment;
import mssu.in.restapi_app.service.AppointmentService;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
	
	@Autowired
	private AppointmentService appointmentService;
	
	@GetMapping("/get")
	public List<Appointment> getAllAppointments(){
		return appointmentService.getAllAppointments();
	}
	
	@PostMapping("/add")
	public void addNewAppointment(@RequestBody Appointment appointment) {
		appointmentService.addNewAppointment(appointment);
	}
	
	@DeleteMapping("/delete/{id}")
	public void deleteAppointment(@PathVariable Integer id) {
		appointmentService.deleteAppointment(id);
	}
	
	@PutMapping("/edit")
	public void editAppointment(@RequestBody Appointment appointment) {
		appointmentService.editAppointment(appointment);
	}
	
	@GetMapping("/get/{id}")
	public Appointment getAppointmentById(@PathVariable Integer id) {
		return appointmentService.getAppointmentById(id);
	}
}
