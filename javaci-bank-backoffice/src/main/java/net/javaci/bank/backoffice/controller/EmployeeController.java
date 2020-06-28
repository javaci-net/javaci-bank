package net.javaci.bank.backoffice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.javaci.bank.db.dao.EmployeeDao;
import net.javaci.bank.db.model.Employee;
import net.javaci.bank.db.model.enumaration.EmployeeStatusType;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeDao employeeDao;
    
    @GetMapping("/create")
    public String renderCreatePage(Model model) {
        model.addAttribute(new Employee());
        return "employee/create";
    }
    
    @PostMapping("/create")
    public String handleCreate(@ModelAttribute Employee employee) {
        employee.setStatus(EmployeeStatusType.INACTIVE);
        employeeDao.save(employee);
        return "employee/list";
    }
    
    @GetMapping("/list")
    public String renderListPage(Model model) {
        model.addAttribute("employees", employeeDao.findAll());
        return "employee/list";
    }
}
