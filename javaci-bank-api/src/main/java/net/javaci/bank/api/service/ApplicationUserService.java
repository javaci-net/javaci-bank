package net.javaci.bank.api.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Customer;
import net.javaci.bank.db.model.Employee;
import net.javaci.bank.db.model.enumaration.CustomerStatusType;

@Service
public class ApplicationUserService implements UserDetailsService {

    @Autowired
    private CustomerDao customerDao;
    
    @Autowired
    @Getter @Setter
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String citizenNumber) throws UsernameNotFoundException {
    	final Optional<Customer> customerOptional = customerDao.findByCitizenNumber(citizenNumber);
        if (customerOptional.isEmpty()) {
        	throw new UsernameNotFoundException("Invalid username or password...");
        }
        Customer c = customerOptional.get();
        
        if (c.getStatus() != CustomerStatusType.ACTIVE) {
        	throw new UsernameNotFoundException("User is not activated or approved");
        }
        
        return new org.springframework.security.core.userdetails.User(c.getCitizenNumber(),
        		c.getPassword(), mapRolesToAuthorities("USER"));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(String role) {
        return Arrays.asList(new SimpleGrantedAuthority(role)); 
    }
}