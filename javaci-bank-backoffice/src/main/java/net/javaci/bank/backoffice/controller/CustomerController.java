package net.javaci.bank.backoffice.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.javaci.bank.backoffice.dto.CustomerListDto;
import net.javaci.bank.backoffice.utils.EntityOneLineDescriptionUtil;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Customer;

@Controller
@RequestMapping("/customer")
public class CustomerController {
	@Autowired
	private CustomerDao customerDao;
	
	@Autowired
	private EntityOneLineDescriptionUtil descriptionUtil;
	
	@GetMapping("/list")
	public String renderListPage(Model model) {
		model.addAttribute("customers", customerDao.findAll());
		
		return "customer/list";
	}

	@PostMapping("/getByfilteredCustomers/")
	@ResponseBody
	public List<CustomerListDto> getByfilteredCustomers(@RequestParam(name = "keyword") String searchKey, Locale locale) {
		List<Customer> allCustomers = customerDao.findAll();
		List<CustomerListDto> returnList = new LinkedList<>();
		
		for (int i = 0; i < allCustomers.size(); i++) {
			String descriiption = descriptionUtil.findCustomerOneLineDescription(allCustomers.get(i), locale);
			if (descriiption.toLowerCase(locale).contains(searchKey.toLowerCase(locale))) {
				returnList.add(new CustomerListDto(allCustomers.get(i).getId(), descriiption));
			}
		}
		
		return returnList;
	}
}
