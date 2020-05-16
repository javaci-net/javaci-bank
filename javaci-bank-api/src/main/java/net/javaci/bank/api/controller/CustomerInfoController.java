package net.javaci.bank.api.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import net.javaci.bank.api.dto.CustomerAddRequestDto;
import net.javaci.bank.api.dto.CustomerDto;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Customer;

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
		return customerDao.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@PostMapping("/add")
	@ResponseBody
	public Long add(@RequestBody CustomerAddRequestDto newCustomerDto) {
		Customer customer = customerDao.save(convertToEntity(newCustomerDto));
		return customer.getId();
	}

	@PostMapping("/update/{id}")
	@ResponseBody
	public boolean update(@RequestBody CustomerDto newCustomerDto, @PathVariable("id") Long id) {
		final Optional<Customer> customerOptional = customerDao.findById(id);
		if (!customerOptional.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer couldnt found by id: " + id);
		}

		Customer customer = convertToEntity(newCustomerDto);
		
		customer.setId(customerOptional.get().getId());
		
		customerDao.save(customer);

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
