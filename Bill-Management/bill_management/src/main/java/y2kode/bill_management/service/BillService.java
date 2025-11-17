package y2kode.bill_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import y2kode.bill_management.entity.Bill;
import y2kode.bill_management.repository.BillRepository;

@Service
public class BillService {
	
	@Autowired
	private BillRepository billRepository;
	
	public List<Bill> getAllBills(){
		return billRepository.getAllBills();

	}
	
	public void addNewBill(Bill bill) {
		billRepository.addNewBill(bill);
	}
	
	public void deleteBill(Integer id) {
		billRepository.deleteBill(id);
	}
	
	public void editBill(Bill bill) {
		billRepository.editBill(bill);
	}
	
	public Bill getBillById(Integer id) {
		return billRepository.getBillById(id);
	}    
}