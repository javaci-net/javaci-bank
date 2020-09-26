package net.javaci.bank.backoffice.service;


import net.javaci.bank.db.dao.EmployeeDao;
import net.javaci.bank.db.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Service
public class EmployeeUserService implements UserDetailsService {

    @Autowired
    private EmployeeDao employeeDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee emp = employeeDao.findByEmail(email);
        if (emp == null){
            throw new UsernameNotFoundException("Invalid username or password...");
        }
        return new org.springframework.security.core.userdetails.User(emp.getEmail(),
                emp.getPassword(),
                mapRolesToAuthorities("USER") );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(String role){
        return Arrays.asList(new SimpleGrantedAuthority(role)); // TODO User ın role lerinden alacagiz
    }

}
