package net.javaci.bank.api.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.javaci.bank.api.dto.TransactionLogDto;
import net.javaci.bank.db.dao.TransactionLogDao;
import net.javaci.bank.db.model.TransactionLog;

@RestController
@RequestMapping("/api/transaction")
public class TransactionLogApi {

    @Autowired
    private TransactionLogDao transactionLogDao;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @GetMapping("/list")
    @ResponseBody
    public List<TransactionLogDto> listAll() {
        return transactionLogDao.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @PostMapping("/deposit")
    @ResponseBody
    public TransactionLogDto deposit(Long accountId, BigDecimal amount) {
        // TODO Implement
        return null;
    }
    
    @PostMapping("/withdraw")
    @ResponseBody
    public TransactionLogDto withdraw(Long accountId, BigDecimal amount) {
        // TODO Implement
        return null;
    }
    
    @PostMapping("/transfer")
    @ResponseBody
    public TransactionLogDto transfer(Long accountId, String toAccountNumber, BigDecimal amount) {
        // TODO Implement
        return null;
    }
    
    /* --------------------------------------------- */
    /* HELPER METHOD(S) */
    /* --------------------------------------------- */
    
    private TransactionLogDto convertToDto(TransactionLog transactionLog) {
        return modelMapper.map(transactionLog, TransactionLogDto.class);
    }
}
