package net.javaci.bank.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountNumberGenerator.class)
public class AccountNumberGeneratorTest {

    @Autowired
    AccountNumberGenerator accountNumberGenerator;

    @Test
    public void testGenerateAccountNumber() {
        String accountNumber = accountNumberGenerator.generateAccountNumber("1234", 987);
        assertEquals("1234-00987", accountNumber);
    }
}