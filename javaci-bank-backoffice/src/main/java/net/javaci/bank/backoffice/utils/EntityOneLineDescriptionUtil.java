package net.javaci.bank.backoffice.utils;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import net.javaci.bank.db.model.Account;
import net.javaci.bank.db.model.Customer;
import net.javaci.bank.util.StringUtil;

@Component
public class EntityOneLineDescriptionUtil {

	@Autowired
	private MessageSource messageSource;
	
	public String findCustomerOneLineDescription(Customer cust, Locale locale) {
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
		
		sb.append(", ");
		sb.append(messageSource.getMessage("customer.email", null, locale));
		sb.append(": ");
		sb.append(cust.getEmail());
		
		sb.append(")");

		return sb.toString();
	}

	public String findAccountOneLineDescription(Account account, Locale locale) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("ID: ");
		sb.append(account.getId());
		sb.append(", ");
		sb.append(messageSource.getMessage("account.accountNumber", null, locale));
		sb.append(": ");
		sb.append(account.getAccountNumber());
		sb.append(", ");
		sb.append(messageSource.getMessage("account.accountName", null, locale));
		sb.append(": ");
		sb.append(account.getAccountName());
		sb.append(", ");
		sb.append(messageSource.getMessage("account.balance", null, locale));
		sb.append(": ");
		sb.append(account.getBalance());
		sb.append(", ");
		sb.append(messageSource.getMessage("account.currency", null, locale));
		sb.append(": ");
		sb.append(account.getCurrency());
		sb.append(", ");
		sb.append(messageSource.getMessage("account.status", null, locale));
		sb.append(": ");
		sb.append(account.getStatus());
		
		
		return sb.toString();
	}
}
