package net.javaci.bank.db.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void testHashCodeForCustomerWithSameIdAndSameCitizenNumber() {
        Customer cust1 = Customer.builder().id(1L).citizenNumber("12345678901").build();
        Customer cust2 = Customer.builder().id(1L).citizenNumber("12345678901").build();
        assertEquals(cust1.hashCode(), cust2.hashCode());
    }
    
    @Test
    void testHashCodeForCustomerWithSameIdAndDifferentCitizenNumber() {
        Customer cust1 = Customer.builder().id(1L).citizenNumber("12345678901").build();
        Customer cust2 = Customer.builder().id(1L).citizenNumber("10987654321").build();
        assertNotEquals(cust1.hashCode(), cust2.hashCode());
    }

    @Test
    void testEqualsWithSameIdAndSameCitizenNumber() {
        Customer cust1 = Customer.builder().id(1L).citizenNumber("12345678901").build();
        Customer cust2 = Customer.builder().id(1L).citizenNumber("12345678901").build();
        assertTrue(cust1.equals(cust2));
    }
    
    @Test
    void testEqualsWithSameIdAndDifferentCitizenNumber() {
        Customer cust1 = Customer.builder().id(1L).citizenNumber("12345678901").build();
        Customer cust2 = Customer.builder().id(1L).citizenNumber("10987654321").build();
        assertFalse(cust1.equals(cust2));
    }

}
