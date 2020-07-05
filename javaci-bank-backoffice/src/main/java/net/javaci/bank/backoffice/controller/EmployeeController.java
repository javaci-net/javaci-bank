package net.javaci.bank.backoffice.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import net.javaci.bank.backoffice.dto.EmployeeCreateDto;
import net.javaci.bank.db.dao.EmployeeDao;
import net.javaci.bank.db.model.Employee;
import net.javaci.bank.db.model.enumaration.EmployeeStatusType;

@Controller
@RequestMapping("/employee")
public class EmployeeController implements WebMvcConfigurer {

    @Autowired
    private EmployeeDao employeeDao;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @GetMapping("/create")
    public String renderCreatePage(Model model) {
        model.addAttribute("employeeDto", new EmployeeCreateDto());
        return "employee/create";
    }
    
    @PostMapping("/create")
    public String handleCreate(@ModelAttribute @Validated EmployeeCreateDto employeeDto, BindingResult bindingResult, Model model) {
        
        if (!employeeDto.getConfirmPassword().equals(employeeDto.getPassword())) {
            bindingResult.addError(new ObjectError("confirmPassword", "Input same password"));
        }
        
        if (bindingResult.hasErrors()) {
            return renderCreatePage(model);
        }
        
        Employee employeeEntity = new Employee();
        modelMapper.map(employeeDto, employeeEntity);
        
        employeeEntity.setStatus(EmployeeStatusType.INACTIVE);
        employeeDao.save(employeeEntity);
        return "redirect:/employee/list";
    }
    
    @GetMapping("/list")
    public String renderListPage(Model model) {
        model.addAttribute("employees", employeeDao.findAll());
        return "employee/list";
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("employee/list").setViewName("employee/list");
    }
}
