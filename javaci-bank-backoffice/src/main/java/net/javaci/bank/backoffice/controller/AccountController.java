package net.javaci.bank.backoffice.controller;

import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.javaci.bank.db.dao.AccountDao;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Customer;

@Controller
@RequestMapping(AccountController.BASE_URL)
public class AccountController {


	public static final String BASE_URL = "/account";

	public static final String LIST_URL = "/list";

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private AccountDao accountDao;

	@GetMapping(LIST_URL)
	public String renderListPage(Model model, @RequestParam(required = false) Long customerId, Locale locale) {
		if (customerId != null) {
			Optional<Customer> optCust = customerDao.findById(customerId);
			if (optCust.isPresent()) {
				Customer customer = optCust.get();
				model.addAttribute("customer", customer);
				model.addAttribute("accounts", accountDao.findAllByCustomer(customer));
			}
		}

		return "account/list";
	}

}
