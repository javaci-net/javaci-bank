package net.javaci.bank.backoffice.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Customer;

@Controller
@RequestMapping("/customer")
public class CustomerController {
	
	@Autowired
	private CustomerDao customerDao;
	
	@GetMapping("/list")
	public String renderListPage(Model model) {
		model.addAttribute("customers", customerDao.findAll());
		
		return "customer/list";
	}
	/*
	
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
	
	private CustomerListDto convertToDto(Customer customer) {
		return modelMapper.map(customer, CustomerListDto.class);
	}
	*/

}
