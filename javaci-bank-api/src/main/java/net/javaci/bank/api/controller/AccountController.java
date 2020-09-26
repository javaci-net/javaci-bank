package net.javaci.bank.api.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import net.javaci.bank.api.dto.AccountSaveDto;
import net.javaci.bank.api.dto.AccountListDto;
import net.javaci.bank.db.dao.AccountDao;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Account;
import net.javaci.bank.db.model.Customer;
import net.javaci.bank.db.model.enumaration.AccountStatusType;
import net.javaci.bank.util.AccountNumberGenerator;

@Slf4j
@RestController
@RequestMapping(AccountController.API_ACCOUNT_BASE_URL)
public class AccountController {

	public static final String API_ACCOUNT_BASE_URL = "/api/account";
	
	@Autowired private AccountDao accountDao;
	@Autowired private CustomerDao customerDao;
	@Autowired private ModelMapper modelMapper;
	@Autowired private AccountNumberGenerator accountNumberGenerator;

	@GetMapping("/list")
	@ResponseBody
	public List<AccountListDto> listAll() {
		return accountDao.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@PostMapping("/add")
	@ResponseBody
	public Long add(@RequestBody AccountSaveDto newAccountDto) {
		
		log.debug("Adding account: {}", newAccountDto);

		// Find and set customer to account
		Account account = convertToEntity(newAccountDto);
		Customer customer = findCustomer(newAccountDto.getCustomerId());
		account.setCustomer(customer);
		
		// Set account number
		int numberOfAccount = accountDao.countByCustomerId(customer.getId());
		String newAccountNo = accountNumberGenerator.generateAccountNumber(customer.getCitizenNumber(), numberOfAccount + 1);
		account.setAccountNumber(newAccountNo);

		account = accountDao.save(account);

		return account.getId();
	}

	@GetMapping("/get")
	@ResponseBody
	public AccountListDto get(Long accountId) {
		Account account = findAccount(accountId);
		return convertToDto(account);
	}

	/**
	 * Changes a given bank account's status to closed.
	 * 
	 * @param accountId the account id
	 */
	@PostMapping("/close")
	@ResponseBody
	public AccountListDto close(@RequestBody Long accountId) {
		Account account = findAccount(accountId);
		account.setStatus(AccountStatusType.CLOSED);
		account = accountDao.save(account);
		return convertToDto(account);
	}

	/* --------------------------------------------- */
	/* HELPER METHOD(S) */
	/* --------------------------------------------- */

	private Customer findCustomer(Long customerId) {
		final Optional<Customer> customerToBeSearched = customerDao.findById(customerId);
		if (!customerToBeSearched.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer does not exists with ID: " + customerId);
		}
		Customer customer = customerToBeSearched.get();
		return customer;
	}

	private Account findAccount(Long accountId) {
		final Optional<Account> accountToBeSearched = accountDao.findById(accountId);
		if (!accountToBeSearched.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account does not exists with ID: " + accountId);
		}
		Account account = accountToBeSearched.get();
		return account;
	}

	private AccountListDto convertToDto(Account account) {
		return modelMapper.map(account, AccountListDto.class);
	}
	
	private Account convertToEntity(AccountSaveDto accountDto) {
		return modelMapper.map(accountDto, Account.class);
	}
}
