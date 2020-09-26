package net.javaci.bank.backoffice.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import net.javaci.bank.db.dao.EmployeeDao;
import net.javaci.bank.db.model.Employee;
import net.javaci.bank.db.model.enumaration.EmployeeStatusType;

@Controller
public class LoginControler {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private EmployeeDao employeeDao;

    @GetMapping("/login")
    public String login(Model model, Principal user) {
    	
    	if (user != null) {
            return "redirect:/";
        }
    	
    	checkDefaultUser();
    	
        return "login";
    }

	public void checkDefaultUser() {
		Employee admin = employeeDao.findByEmail("admin");
		if (admin == null) {
			admin = new Employee();
			admin.setEmail("admin");
			admin.setFirstName("Koray");
			admin.setLastName("Gecici");
			admin.setPassword(passwordEncoder.encode("admin"));
			admin.setCitizenNumber("123456789");
			admin.setStatus(EmployeeStatusType.ACTIVE);
			employeeDao.save(admin);
		}
	}
    
}
