package net.javaci.bank.backoffice.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/list")
	public String renderListPage(Model model) {
		model.addAttribute("employees", employeeDao.findAll());
		return "employee/list";
	}

	@GetMapping("/create")
	public String renderCreatePage(Model model) {
		model.addAttribute("employeeDto", new EmployeeCreateDto());
		return "employee/create";
	}

	@PostMapping("/create")
	public String handleCreate(@ModelAttribute @Validated EmployeeCreateDto employeeDto, BindingResult bindingResult,
			Model model) {

		if (bindingResult.hasErrors() || !employeeDto.getConfirmPassword().equals(employeeDto.getPassword())) {
			return "error/javaScriptValidationIgnored";
		}

		Employee employeeEntity = new Employee();
		modelMapper.map(employeeDto, employeeEntity);

		// Encoding password
		employeeEntity.setPassword(passwordEncoder.encode(employeeEntity.getPassword()));

		employeeDao.save(employeeEntity);

		return "redirect:/employee/list";
	}

	@GetMapping("/profile")
	public String renderProfilePage(Model model, Principal user) {

		Employee employeeEntity = employeeDao.findByEmail(user.getName());
		EmployeeCreateDto employeeDto = new EmployeeCreateDto();

		modelMapper.map(employeeEntity, employeeDto);

		model.addAttribute("employeeDto", employeeDto);
		
		return "employee/update";
	}

	// FIXME role kontrolu yap
	@GetMapping("/update/{id}")
	public String renderUpdatePage(Model model, @PathVariable("id") Long id) {
		Employee employeeEntity = employeeDao.findById(id).get();
		EmployeeCreateDto employeeDto = new EmployeeCreateDto();

		modelMapper.map(employeeEntity, employeeDto);

		model.addAttribute("employeeDto", employeeDto);
		
		return "employee/update";
	}

	// FIXME role kontrolu yap
	@PostMapping("/update/{id}")
	public String handleUpdate(@ModelAttribute @Valid EmployeeCreateDto employeeDto, BindingResult bindingResult,
			Model model, @PathVariable("id") Long id) {

		if (bindingResult.hasErrors()) {
			return "error/javaScriptValidationIgnored";
		}

		Employee employeeEntity = employeeDao.findById(id).get();
		modelMapper.map(employeeDto, employeeEntity);
		employeeDao.save(employeeEntity);

		return "redirect:/employee/list";
	}

	@GetMapping("/checkCitizenNumber/{citizenNumber}")
	@ResponseBody
	public boolean checkCitizenNumber(@PathVariable("citizenNumber") String citizenNumber) {
		return employeeDao.existsByCitizenNumber(citizenNumber);
	}

}
