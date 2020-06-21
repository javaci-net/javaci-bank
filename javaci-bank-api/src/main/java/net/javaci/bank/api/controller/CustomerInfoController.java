package net.javaci.bank.api.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import net.javaci.bank.api.dto.CustomerAddRequestDto;
import net.javaci.bank.api.dto.CustomerDto;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Customer;

@Slf4j
@RestController
@RequestMapping("/api/customer-info")
public class CustomerInfoController {

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping("/list")
	@ResponseBody
	public List<CustomerDto> listAll() {
		log.debug("Listing all customers in CustomerInfoController");
		return customerDao.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@PostMapping("/add")
	@ResponseBody
	public Long add(@RequestBody CustomerAddRequestDto newCustomerDto) {
		log.debug("Adding customer: " + newCustomerDto);
		Optional<Customer> c = customerDao.findByCitizenNumber(newCustomerDto.getCitizenNumber());
		if (c.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Citizen number already exist: " + newCustomerDto.getCitizenNumber());
		}
		Customer customer = customerDao.save(convertToEntity(newCustomerDto));
		log.info("Customer added with id: " + customer.getId());
		return customer.getId();
	}

	@PutMapping("/update/{id}")
	@ResponseBody
	public boolean update(@RequestBody CustomerDto newCustomerDto, @PathVariable("id") Long id) {
		final Optional<Customer> customerOptional = customerDao.findById(id);
		if (!customerOptional.isPresent()) {
			log.info("Customer not found with id:" + id);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer couldnt found by id: " + id);
		}

		Customer customer = convertToEntity(newCustomerDto);
		
		customer.setId(customerOptional.get().getId());
		
		customerDao.save(customer);
		log.info(String.format("Customer updated: %s", customer));
		return true;
	}

	/* --------------------------------------------- */
	/* HELPER METHOD(S) */
	/* --------------------------------------------- */

	private CustomerDto convertToDto(Customer customer) {
		return modelMapper.map(customer, CustomerDto.class);
	}

	private Customer convertToEntity(CustomerDto customerDto) {
		return modelMapper.map(customerDto, Customer.class);
	}

}
