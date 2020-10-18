package net.javaci.bank.api.controller;

import java.security.Principal;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import net.javaci.bank.api.dto.CustomerListDto;
import net.javaci.bank.api.dto.CustomerSaveDto;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Customer;
import net.javaci.bank.db.model.enumeration.CustomerStatusType;

@Slf4j
@RestController
@RequestMapping(CustomerApi.BASE_URL)
public class CustomerApi {

	public static final String BASE_URL = "/api/customer";

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/register")
	@ResponseBody
	public Long add(@RequestBody CustomerSaveDto customerSaveDto) {
		log.debug("Adding customer: " + customerSaveDto);
		Optional<Customer> c = customerDao.findByCitizenNumber(customerSaveDto.getCitizenNumber());
		
		if (c.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Citizen number already exist: " + customerSaveDto.getCitizenNumber());
		}
		
		Customer customer = convertToEntity(customerSaveDto);
		
		// FIXME backoffice tamamlanınca kaldırılacak
		customer.setStatus(CustomerStatusType.ACTIVE);
		
		customer.setPassword(passwordEncoder.encode(customer.getPassword()));
		
		customer = customerDao.save(customer);
		log.info("Customer added with id: " + customer.getId());
		return customer.getId();
	}

	@PutMapping("/update")
	@ResponseBody
	public boolean update(@RequestBody CustomerSaveDto customerSaveDto, Principal user) {
		
		Optional<Customer> customerDB = customerDao.findByCitizenNumber(user.getName());

		Customer customer = convertToEntity(customerSaveDto);
		
		customer.setId(customerDB.get().getId());
		
		customerDao.save(customer);
		log.info(String.format("Customer updated: %s", customer));
		return true;
	}
	
	@GetMapping("/getInfo")
	@ResponseBody
	public CustomerListDto getInfo(Principal user) {
		
		Customer customerDB = customerDao.findByCitizenNumber(user.getName()).get();
		
		CustomerListDto customerListDto = convertToDto(customerDB);
		
		log.info(String.format("Customer updated: %s", customerDB.getId()));
		
		return customerListDto;
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
