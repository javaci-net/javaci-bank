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

import net.javaci.bank.api.dto.AccountDto;
import net.javaci.bank.db.dao.AccountDao;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Account;
import net.javaci.bank.db.model.Customer;

@RestController
@RequestMapping("/api/account-info")
public class AccountInfoController {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/list")
    @ResponseBody
    public List<AccountDto> listAll() {
        return accountDao.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/add")
    @ResponseBody
    public AccountDto add(@RequestBody AccountDto newAccountDto) {
        return convertToDto( accountDao.save(convertToEntity(newAccountDto)) );
    }

    private AccountDto convertToDto(Account account) {
        return modelMapper.map(account, AccountDto.class);
    }

    private Account convertToEntity(AccountDto accountDto) {
        final Optional<Customer> customer = customerDao.findById(accountDto.getCustomer());
        if (!customer.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer with ID does not exists");
        }
        final Account account = modelMapper.map(accountDto, Account.class);
        account.setCustomer(customer.get());
        return account;
    }
}
