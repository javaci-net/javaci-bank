package net.javaci.bank.api.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Customer;

@Component
public class JwtAuthenticationHelper {

    @Autowired private CustomerDao customerDao;
    
    /**
     * @see JwtTokenVerifierFilter uses principal as loggedin customer's citizen number
     * @return
     */
    public String findAuthenticatedCitizenNumber() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) authentication.getPrincipal();
    }
    
    /**
     * @see JwtTokenVerifierFilter uses principal as loggedin customer's citizen number
     * @return
     */
    public Customer findAuthenticatedCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principal = (String) authentication.getPrincipal();
        return customerDao.findByCitizenNumber(principal).get();
    }
}
