package net.javaci.bank.backoffice.dto;
import net.javaci.bank.util.AccountNumberGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

// @RunWith(SpringRunner.class)
// @SpringBootTest()
public class EmployeeCreateDtoTest {

    @Test
    public void testToString() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setCitizenNumber("123");
        dto.setFirstName("Koray");
        dto.setLastName("gecici");
        dto.setPassword("abcd.1234");
        dto.setConfirmPassword("abcd.1234");
        String toString = dto.toString();

        assertFalse( toString.contains("password"), "toString of Employee should not contain password");
        assertFalse( toString.contains("confirmPassword"), "toString of Employee should not contain confirmPassword");

    }
}
