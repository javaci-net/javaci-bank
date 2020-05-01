package net.javaci.bank.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
		return customerDao.findAll()
				.stream()
				.map( customer -> modelMapper.map(customer, CustomerDto.class) )
				.collect(Collectors.toList());
	}
	
	@PostMapping("/add")
	@ResponseBody
	public Customer add(@RequestBody CustomerDto newCustomerDto) {
		return customerDao.save(modelMapper.map(newCustomerDto, Customer.class));
	}
	
}
