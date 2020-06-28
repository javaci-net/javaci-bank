package net.javaci.bank.backoffice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @GetMapping("/create")
    public String renderCreatePage(Model model) {
        return "employee/create";
    }
    
    @PostMapping("/create")
    public String handleCreate(Model model) {
    	System.out.println(model);
        return "employee/list";
    }
    
    @GetMapping("/list")
    public String renderListPage(Model model) {
        return "employee/list";
    }
}
