package com.customerManagement.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.customerManagement.demo.entity.Customer;

@SuppressWarnings("unused")
@Repository
public class CustomerRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	// Get all customers
    public List<Customer> getAllCustomers() {
        String sql = "SELECT * FROM customers";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Customer.class));
    }
    
    //  Add a new customer
    public void addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (firstName, lastName, email, phoneNumber, address, city, state, postalCode, createdAt, updatedAt) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getAddress(),
                customer.getCity(),
                customer.getState(),
                customer.getPostalCode(),
                customer.getCreatedAt(),
                customer.getUpdatedAt());
    }


    //  Delete customer by ID
    public void deleteCustomer(Integer id) {
        String sql = "DELETE FROM customers WHERE id=?";
        jdbcTemplate.update(sql, id);
    }

    //  Get customer by ID
    public Optional<Customer> getCustomerById(Integer id) {
        String sql = "SELECT * FROM customers WHERE id=?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Customer.class), id);
            return Optional.of(customer);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    //  Search customers by first or last name
    public List<Customer> searchCustomersByName(String name) {
        String sql = "SELECT * FROM customers WHERE firstName LIKE ? OR lastName LIKE ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Customer.class), "%" + name + "%", "%" + name + "%");
    }

    public Customer updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET firstName=?, lastName=?, email=?, phoneNumber=?, address=?, city=?, state=?, postalCode=?, updatedAt=? WHERE id=?";
        jdbcTemplate.update(sql,
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getAddress(),
                customer.getCity(),
                customer.getState(),
                customer.getPostalCode(),
                customer.getUpdatedAt(),
                customer.getId());
        return customer;
    }

}
