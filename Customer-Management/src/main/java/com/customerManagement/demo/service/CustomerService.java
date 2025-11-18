package com.customerManagement.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.customerManagement.demo.entity.Customer;
import com.customerManagement.demo.repository.CustomerRepository;

@Service
public class CustomerService {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	public List<Customer> getAllCustomers() {
        return customerRepository.getAllCustomers();
    }
	
	public Customer updateCustomer(Customer customer) {
		return customerRepository.updateCustomer(customer);
	}
	
	public void deleteCustomer(Integer id) {
	    customerRepository.deleteCustomer(id);
	}

	public void addCustomer(Customer customer) {
	    customerRepository.addCustomer(customer);
	}

	public Optional<Customer> getCustomerById(Integer id) {
	    return customerRepository.getCustomerById(id);
	}
	
}
