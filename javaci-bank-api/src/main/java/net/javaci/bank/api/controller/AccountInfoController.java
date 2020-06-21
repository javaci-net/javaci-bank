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

import net.javaci.bank.api.dto.AccountAddRequestDto;
import net.javaci.bank.api.dto.AccountDto;
import net.javaci.bank.db.dao.AccountDao;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Account;
import net.javaci.bank.db.model.Customer;
import net.javaci.bank.util.AccountNumberGenerator;

@Slf4j
@RestController
@RequestMapping("/api/account-info")
public class AccountInfoController {

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private AccountNumberGenerator accountNumberGenerator;

	@GetMapping("/list")
	@ResponseBody
	public List<AccountDto> listAll() {
		return accountDao.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@PostMapping("/add")
	@ResponseBody
	public Long add(@RequestBody AccountAddRequestDto newAccountDto) {
		log.debug("Adding customer: " + newAccountDto);
		final Optional<Customer> customerOptional = customerDao.findById(newAccountDto.getCustomer());
		if (!customerOptional.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer does not exists with ID: " + newAccountDto.getCustomer());
		}

		Account account = convertToEntity(newAccountDto);

		Customer customer = customerOptional.get();
		account.setCustomer(customer);
		int numberOfAccount = accountDao.countByCustomerId(customer.getId());

		account.setAccountNumber(
				accountNumberGenerator.generateAccountNumber(customer.getCitizenNumber(), numberOfAccount + 1));

		account = accountDao.save(account);

		return account.getId();
	}

	@GetMapping("/get")
	@ResponseBody
	public AccountDto get(Long accountId) {
		final Optional<Account> account = accountDao.findById(accountId);
		if (!account.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account does not exists with ID: " + accountId);
		}

		return convertToDto(account.get());
	}

	/**
	 * Changes a given bank account's status to closed.
	 * 
	 * @param accountId the account id
	 */
	@PostMapping("/close")
	@ResponseBody
	public AccountDto close(@RequestBody Long accountId) {
		// TODO Implement
		return null;
	}

	/* --------------------------------------------- */
	/* HELPER METHOD(S) */
	/* --------------------------------------------- */

	private AccountDto convertToDto(Account account) {
		return modelMapper.map(account, AccountDto.class);
	}
	
	private Account convertToEntity(AccountAddRequestDto accountDto) {
		return modelMapper.map(accountDto, Account.class);
	}
}
