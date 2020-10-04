package net.javaci.bank.backoffice.controller;

import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.javaci.bank.backoffice.utils.EntityOneLineDescriptionUtil;
import net.javaci.bank.db.dao.AccountDao;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.dao.TransactionLogDao;
import net.javaci.bank.db.model.Account;
import net.javaci.bank.db.model.Customer;

@Controller
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private TransactionLogDao transactionDao;
	
	@Autowired
	private EntityOneLineDescriptionUtil descriptionUtil;
	
	@GetMapping("/list")
	public String renderListPage(Model model,  @RequestParam(required = false) Long accountId, Locale locale) {
		if (accountId == null) {
			model.addAttribute("customerName", null);
		} else {
			Optional<Account> aptAcc = accountDao.findById(accountId);
			if (aptAcc.isPresent() == false) {
				model.addAttribute("customerName", null);
			} else {
				Account account =  aptAcc.get();
				Customer customer = account.getCustomer();
				
				model.addAttribute("customerName", descriptionUtil.findCustomerOneLineDescription(customer, locale));
				model.addAttribute("accountName", descriptionUtil.findAccountOneLineDescription(account, locale));
				model.addAttribute("transactions", transactionDao.findAllByAccount(account));
			}
		}
		return "transaction/list";
	}
	
}
