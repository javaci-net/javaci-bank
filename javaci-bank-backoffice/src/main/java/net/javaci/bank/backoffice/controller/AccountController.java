package net.javaci.bank.backoffice.controller;

import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.javaci.bank.backoffice.utils.EntityOneLineDescriptionUtil;
import net.javaci.bank.db.dao.AccountDao;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Customer;
import net.javaci.bank.util.StringUtil;

@Controller
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private EntityOneLineDescriptionUtil descriptionUtil;

	@GetMapping("/list")
	public String renderListPage(Model model, @RequestParam(required = false) Long customerId, Locale locale) {
		if (customerId == null) {
			model.addAttribute("customerName", null);
		} else {
			Optional<Customer> optCust = customerDao.findById(customerId);
			if (optCust.isPresent() == false) {
				model.addAttribute("customerName", null);
			} else {
				Customer customer = optCust.get();
				model.addAttribute("customerName", descriptionUtil.findCustomerOneLineDescription(customer, locale));
				model.addAttribute("accounts", accountDao.findAllByCustomer(customer));
			}
		}

		return "account/list";
	}

}
