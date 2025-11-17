package y2kode.bill_management.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import y2kode.bill_management.entity.Bill;

@Repository
public class BillRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Bill> getAllBills() {
		String sql = "SELECT id, biller, description, amount, bill_date as billDate, payment_date as paymentDate, paymentmode as paymentMode, paymentstatus as paymentStatus FROM bill";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Bill.class));
	}
	
	public void addNewBill(Bill bill) {
		String sql = "insert into bill(biller, description, amount, paymentMode, paymentStatus) values (?,?,?,?,?)";
		jdbcTemplate.update(
				sql,
				bill.getBiller(),
				bill.getDescription(),
				bill.getAmount(),
				bill.getPaymentMode().name(),
				bill.getPaymentStatus().name()
		);
	}

	public void editBill(Bill bill) {
		String sql="update bill set biller=?, description=?, amount=?, paymentMode=?, paymentStatus=? where id=?";
		jdbcTemplate.update(
				sql,
				bill.getBiller(),
				bill.getDescription(),
				bill.getAmount(),
				bill.getPaymentMode().name(),
				bill.getPaymentStatus().name(),
				bill.getId()
		);
	}
	
	public void deleteBill(Integer id) {
		String sql="delete from bill where id=?";
		jdbcTemplate.update(sql,id);
	}
	
	public Bill getBillById(Integer id) {
		String sql = "SELECT id, biller, description, amount, bill_date as billDate, payment_date as paymentDate, paymentmode as paymentMode, paymentstatus as paymentStatus FROM bill WHERE id=?";
		return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Bill.class), id);
	}
}
