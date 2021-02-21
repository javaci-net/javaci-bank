package net.javaci.bank.backoffice.controller;

import java.security.Principal;
import java.util.Optional;

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
public class EmployeeController extends AbstractController {

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
		if (canModifyData() == false) {
			return "error/notAdminError";
		}

		model.addAttribute("employeeDto", new EmployeeCreateDto());
		return "employee/create";
	}

	@PostMapping("/create")
	public String handleCreate(@ModelAttribute @Validated EmployeeCreateDto employeeDto, BindingResult bindingResult) {
		if (canModifyData() == false) {
			return "error/notAdminError";
		}

		if (bindingResult.hasErrors() || !employeeDto.getConfirmPassword().equals(employeeDto.getPassword())) {
			return "error/javaScriptValidationIgnored";
		}

		Employee employeeEntity = new Employee();
		modelMapper.map(employeeDto, employeeEntity);

		// Encoding password
		employeeEntity.setPassword(passwordEncoder.encode(employeeDto.getPassword()));

		employeeDao.save(employeeEntity);

		return "redirect:/employee/list";
	}

	@GetMapping("/update/{id}")
	public String renderUpdatePage(Model model, @PathVariable("id") Long id, Principal user) {
		
		if (!canModifyData() && !isLogginUser(id)) {
			return "error/notAdminError";
		}
		
		Optional<Employee> optional = employeeDao.findById(id);
		if (optional.isEmpty()) {
			return "error/404";
		}
		
		Employee employeeEntity = optional.get();
		EmployeeCreateDto employeeDto = new EmployeeCreateDto();

		modelMapper.map(employeeEntity, employeeDto);

		model.addAttribute("employeeDto", employeeDto);

		return "employee/update";
	}

	@PostMapping("/update/{id}")
	public String handleUpdate(@ModelAttribute @Valid EmployeeCreateDto employeeDto, BindingResult bindingResult,
			Model model, @PathVariable("id") Long id, Principal user) {

		if (!canModifyData() && !isLogginUser(id)) {
			return "error/notAdminError";
		}
		
		if (id == 1) {
			return "error/adminUserUpdateError";
		}

		if (bindingResult.hasErrors()) {
			return "error/javaScriptValidationIgnored";
		}

		Employee employeeEntity = employeeDao.findById(id).get();
		modelMapper.map(employeeDto, employeeEntity);
		
		employeeDao.save(employeeEntity);

		return "redirect:/employee/list";
	}
	
	@PostMapping("/delete/{id}")
	public String handleUpdate(@PathVariable("id") Long id) {
		
		if (employeeDao.existsById(id) == false) {
			return "error/404";
		}
		
		employeeDao.deleteById(id);
		
		return "redirect:/employee/list";
	}
			

	@GetMapping("/checkCitizenNumber/{citizenNumber}")
	@ResponseBody
	public boolean checkCitizenNumber(@PathVariable("citizenNumber") String citizenNumber) {
		return employeeDao.existsByCitizenNumber(citizenNumber);
	}

}
