package y2kode.bill_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import y2kode.bill_management.entity.Bill;
import y2kode.bill_management.service.BillService;

@RestController
@RequestMapping("/bills")
@CrossOrigin(origins = "*")

public class BillController {
	
	@Autowired
	private BillService billService;
	
	@GetMapping("/get")
	public List<Bill> getAllBills(){
		return billService.getAllBills();
	}
	
	@PostMapping("/add")
	public void addNewBill(@RequestBody Bill bill) {
		billService.addNewBill(bill);
	}
	
	@DeleteMapping("/delete/{id}")
	public void deleteBill(@PathVariable Integer id) {
		billService.deleteBill(id);
	}
	
	@PutMapping("/edit")
	public void editBill(@RequestBody Bill bill) {
		billService.editBill(bill);
	}
	
	@GetMapping("/get/{id}")
	public Bill getBillById(@PathVariable Integer id) {
		return billService.getBillById(id);
	}    
}