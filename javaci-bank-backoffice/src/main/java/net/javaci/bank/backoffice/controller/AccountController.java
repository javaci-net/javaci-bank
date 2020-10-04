package net.javaci.bank.backoffice.controller;

import net.javaci.bank.db.dao.AccountDao;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Customer;
import net.javaci.bank.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping(AccountController.BASE_URL)
public class AccountController {

	public static final String BASE_URL = "/account";

	public static final String LIST_URL = "/list";

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private AccountDao accountDao;

	@GetMapping(LIST_URL)
	public String renderListPage(Model model, @RequestParam(required = false) Long customerId, Locale locale) {
		if (customerId == null) {
			model.addAttribute("customerName", null);
		} else {
			Optional<Customer> optCust = customerDao.findById(customerId);
			if (optCust.isPresent() == false) {
				model.addAttribute("customerName", null);
			} else {
				Customer cust = optCust.get();
				model.addAttribute("customerName", findFullName(cust, locale));
				model.addAttribute("accounts", accountDao.findAllByCustomer(cust));
			}
		}

		return "account/list";
	}

	private Object findFullName(Customer cust, Locale locale) {
		StringBuilder sb = new StringBuilder();
		sb.append(cust.getFirstName());
		sb.append(" ");

		if (StringUtil.isBlankString(cust.getMiddleName())) {
			sb.append(cust.getMiddleName());
			sb.append(" ");
		}

		sb.append(cust.getLastName());
		sb.append(" (ID: ");
		sb.append(cust.getId());
		sb.append(", ");
		sb.append(messageSource.getMessage("customer.citizenNumber", null, locale));
		sb.append(": ");
		sb.append(cust.getCitizenNumber());
		sb.append(")");

		return sb.toString();
	}
}
