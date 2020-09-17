package net.javaci.bank.api.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import net.javaci.bank.api.dto.CustomerSaveDto;
import net.javaci.bank.api.dto.CustomerListDto;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Customer;
import net.javaci.bank.db.model.enumaration.CustomerStatusType;

@Slf4j
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/list")
	@ResponseBody
	public List<CustomerListDto> listAll() {
		log.debug("Listing all customers in CustomerInfoController");
		return customerDao.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@PostMapping("/register")
	@ResponseBody
	public Long add(@RequestBody CustomerSaveDto customerSaveDto) {
		log.debug("Adding customer: " + customerSaveDto);
		Optional<Customer> c = customerDao.findByCitizenNumber(customerSaveDto.getCitizenNumber());
		if (c.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Citizen number already exist: " + customerSaveDto.getCitizenNumber());
		}
		Customer customer = convertToEntity(customerSaveDto);
		
		// FIXME 
		customer.setStatus(CustomerStatusType.ACTIVE);
		
		customer.setPassword(passwordEncoder.encode(customer.getPassword()));
		
		customer = customerDao.save(customer);
		log.info("Customer added with id: " + customer.getId());
		return customer.getId();
	}

	@PutMapping("/update/{id}")
	@ResponseBody
	public boolean update(@RequestBody CustomerSaveDto customerSaveDto, @PathVariable("id") Long id) {
		final Optional<Customer> customerOptional = customerDao.findById(id);
		if (!customerOptional.isPresent()) {
			log.info("Customer not found with id:" + id);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer couldnt found by id: " + id);
		}

		Customer customer = convertToEntity(customerSaveDto);
		
		customer.setId(customerOptional.get().getId());
		
		customerDao.save(customer);
		log.info(String.format("Customer updated: %s", customer));
		return true;
	}
	
	@GetMapping("/getByCitizenNumber/{citizenNumber}")
	@ResponseBody
	public CustomerListDto getByCitizenNumber(@PathVariable("citizenNumber") String citizenNumber) {
		
		final Optional<Customer> customerOptional = customerDao.findByCitizenNumber(citizenNumber);
		if (!customerOptional.isPresent()) {
			log.info("Customer not found with citizenNumber:" + citizenNumber);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer couldnt found by citizenNumber: " + citizenNumber);
		}

		Customer customer = customerOptional.get();
		
		return convertToDto(customer);
	}

	/* --------------------------------------------- */
	/* HELPER METHOD(S) */
	/* --------------------------------------------- */

	private CustomerListDto convertToDto(Customer customer) {
		return modelMapper.map(customer, CustomerListDto.class);
	}

	private Customer convertToEntity(CustomerSaveDto customerSaveDto) {
		return modelMapper.map(customerSaveDto, Customer.class);
	}

}
