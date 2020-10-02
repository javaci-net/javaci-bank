package net.javaci.bank.backoffice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.javaci.bank.db.dao.CustomerDao;

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

}
