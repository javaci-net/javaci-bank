package net.javaci.bank.api.controller;

import net.javaci.bank.api.dto.CustomerAddRequestDto;
import net.javaci.bank.db.dao.CustomerDao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CustomerInfoControllerTest {

    @Autowired
    private CustomerInfoController controller;

    @Autowired
    private CustomerDao customerDao;

    private CustomerAddRequestDto createCustomerAddRequestDto() {
        CustomerAddRequestDto dto = new CustomerAddRequestDto();
        dto.setCitizenNumber("123");
        dto.setPassword("123");
        dto.setStatus("ACTIVE");
        dto.setBirthDate(LocalDate.now());
        dto.setName("Koray");
        dto.setPhoneNumber("5548714944");
        return dto;
    }

    @BeforeEach
    public void clearCustomerInfo() {
        customerDao.deleteAll();
    }

    @Test
    public void customerAdd() {
        CustomerAddRequestDto dto = createCustomerAddRequestDto();
        Long id = controller.add(dto);
        assertEquals(controller.listAll().size(), 1);
        assertTrue(controller.listAll().stream().anyMatch((c) -> id.equals(c.getId())) );
    }

    @Test
    public void customerAddDuplicateCitizenNumber() {
        CustomerAddRequestDto dto = createCustomerAddRequestDto();
        controller.add(dto);
        assertThrows(ResponseStatusException.class, () -> {
            controller.add(dto); } ) ;
    }

    @Test
    public void customerUpdate() {
        final String newNAME = "NEW NAME";
        CustomerAddRequestDto dto = createCustomerAddRequestDto();
        Long id = controller.add(dto);
        dto.setName(newNAME);
        controller.update(dto, id);
        assertEquals(controller.listAll().size(), 1);
        assertTrue(controller.listAll().stream().anyMatch((c) -> newNAME.equals(c.getName())) );
    }

}
