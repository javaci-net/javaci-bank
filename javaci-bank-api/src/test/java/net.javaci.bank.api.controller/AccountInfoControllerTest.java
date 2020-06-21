package net.javaci.bank.api.controller;

import net.javaci.bank.api.dto.AccountAddRequestDto;
import net.javaci.bank.api.dto.CustomerAddRequestDto;
import net.javaci.bank.db.dao.AccountDao;
import net.javaci.bank.db.dao.CustomerDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AccountInfoControllerTest {


    @Autowired
    private AccountInfoController controller;

    @Autowired
    private AccountDao accountDao;

    private AccountAddRequestDto createAccountAddRequestDto() {
        return null;
    }

    @BeforeEach
    public void clearAccounts() {
        accountDao.deleteAll();
    }

    @Test
    public void accountAdd() {

    }

}
