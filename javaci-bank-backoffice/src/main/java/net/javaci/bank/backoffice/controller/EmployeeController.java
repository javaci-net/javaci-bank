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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.javaci.bank.backoffice.dto.EmployeeCreateDto;
import net.javaci.bank.db.dao.EmployeeDao;
import net.javaci.bank.db.model.Employee;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

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
	public String handleCreate(@ModelAttribute @Validated EmployeeCreateDto employeeDto, BindingResult bindingResult,
			Model model) {

		if (!validateInput(employeeDto, bindingResult, model)) {
			return "redirect:error/javaScriptValidationIgnored";
		}

		Employee employeeEntity = new Employee();
		modelMapper.map(employeeDto, employeeEntity);
		employeeDao.save(employeeEntity);
		
		return "redirect:/employee/list";
	}


	private boolean validateInput(EmployeeCreateDto employeeDto, BindingResult bindingResult, Model model) {
		if (!employeeDto.getConfirmPassword().equals(employeeDto.getPassword())) {
			bindingResult.addError(new ObjectError("confirmPassword", "Input same password"));
			return false;
		}

		return true;
	}

	@GetMapping("/update/{id}")
	public String renderUpdatePage(Model model, @PathVariable("id") Long id) {
		Employee employeeEntity = employeeDao.findById(id).get();
		EmployeeCreateDto employeeDto = new EmployeeCreateDto();
		
		modelMapper.map(employeeEntity, employeeDto);
		
		model.addAttribute("employeeDto", employeeDto);
		return "employee/update";
	}

	@PostMapping("/update/{id}")
	public String handleUpdate(@ModelAttribute @Validated EmployeeCreateDto employeeDto, BindingResult bindingResult,
			Model model, @PathVariable("id") Long id) {
		if (!validateInput(employeeDto, bindingResult, model)) {
			return "redirect:error/javaScriptValidationIgnored";
		}

		Employee employeeEntity = employeeDao.findById(id).get();
		modelMapper.map(employeeDto, employeeEntity);
		employeeDao.save(employeeEntity);
		
		return "redirect:/employee/list";
	}

	@GetMapping("/list")
	public String renderListPage(Model model) {
		model.addAttribute("employees", employeeDao.findAll());
		return "employee/list";
	}
}