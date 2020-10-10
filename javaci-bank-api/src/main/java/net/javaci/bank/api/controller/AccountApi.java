package net.javaci.bank.api.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import net.javaci.bank.api.dto.AccountListDto;
import net.javaci.bank.api.dto.AccountSaveDto;
import net.javaci.bank.api.jwt.JwtAuthenticationHelper;
import net.javaci.bank.db.dao.AccountDao;
import net.javaci.bank.db.model.Account;
import net.javaci.bank.db.model.Customer;
import net.javaci.bank.db.model.enumeration.AccountStatusType;
import net.javaci.bank.util.AccountNumberGenerator;

@Api(description = "User Bank Accounts API for the logged-in customer")
@Slf4j
@RestController
@RequestMapping(AccountApi.API_ACCOUNT_BASE_URL)
public class AccountApi {

	public static final String API_ACCOUNT_BASE_URL = "/api/account";

	@Autowired private AccountDao accountDao;
	@Autowired private ModelMapper modelMapper;
	@Autowired private AccountNumberGenerator accountNumberGenerator;
	@Autowired private JwtAuthenticationHelper authHelper;
	
	@ApiOperation("Returns list of all Accounts for the logged-in customer")
	@GetMapping("/list")
	@ResponseBody
	public List<AccountListDto> listAll() {  
	    Customer customer = authHelper.findAuthenticatedCustomer();
		return accountDao.findAllByCustomer(customer)
		        .stream().map(this::convertToDto)
		        .collect(Collectors.toList());
	}

    
	@ApiOperation("Create account for the logged-in customer")
	@PostMapping("/create")
	@ResponseBody
	public Long create(@RequestBody AccountSaveDto newAccountDto) {
	    
		// TODO ayni para biriinden aktif iki hesap olamaz currency ye uniq index
		// atilabilir
		log.debug("Create account: {}", newAccountDto);

		// Find and set customer to account
		Account account = convertToEntity(newAccountDto);
		Customer customer = authHelper.findAuthenticatedCustomer();
		account.setCustomer(customer);

		// Set account number
		int numberOfAccount = accountDao.countByCustomerId(customer.getId());
		String newAccountNo = accountNumberGenerator.generateAccountNumber(customer.getCitizenNumber(),
				numberOfAccount + 1);
		account.setAccountNumber(newAccountNo);
		
		// persist to db
		account = accountDao.save(account);

		return account.getId();
	}

	@GetMapping("/getInfo")
	@ResponseBody
	public AccountListDto getInfo(
			@ApiParam(value = "Id of the account. Cannot be empty.", required = true, example = "1") 
			Long accountId
	) {
		Account account = findAccount(accountId);
		validateAccountCustomer(accountId, account);
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
		validateAccountCustomer(accountId, account);

		account.setStatus(AccountStatusType.CLOSED);
		account = accountDao.save(account);
		return convertToDto(account);
	}

	/* --------------------------------------------- */
	/* HELPER METHOD(S) */
	/* --------------------------------------------- */

	private void validateAccountCustomer(Long accountId, Account account) {
        Customer accountCustomer = account.getCustomer();
        Customer customer = authHelper.findAuthenticatedCustomer();
        if (!customer.equals(accountCustomer)) {
            String reason = String.format("Account {} does not belong to the authenticated customer {}: ", accountId, customer.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
        }
    }
	
	private Account findAccount(Long accountId) {
		final Optional<Account> accountToBeSearched = accountDao.findById(accountId);
		if (!accountToBeSearched.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account does not exists with ID: " + accountId);
		}
		return accountToBeSearched.get();
	}

	private AccountListDto convertToDto(Account account) {
		return modelMapper.map(account, AccountListDto.class);
	}

	private Account convertToEntity(AccountSaveDto accountDto) {
		return modelMapper.map(accountDto, Account.class);
	}
}
