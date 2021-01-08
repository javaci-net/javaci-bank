package net.javaci.bank.backoffice.controller;

import java.security.Principal;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.javaci.bank.backoffice.dto.EmployeeCreateDto;
import net.javaci.bank.backoffice.dto.PasswordChangeDto;
import net.javaci.bank.db.dao.EmployeeDao;
import net.javaci.bank.db.model.Employee;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@GetMapping("/profile")
	public String renderProfilePage(Model model, Principal user) {
		Employee employeeEntity = employeeDao.findByEmail(user.getName());
		EmployeeCreateDto employeeDto = new EmployeeCreateDto();

		modelMapper.map(employeeEntity, employeeDto);

		model.addAttribute("employeeDto", employeeDto);
		
		return "employee/update";
	}
	
	
	@GetMapping("/changePassword")
	public String renderChangePasswordPage(Model model) {
		model.addAttribute("passwordChangeDto", new PasswordChangeDto());
		
		return "user/changePassword";
	}
	
	@PostMapping("/changePassword")
	public String handleChangePassword(Model model, @ModelAttribute @Validated PasswordChangeDto passwordChangeDto, BindingResult bindingResult, Principal user) {
		
		if (bindingResult.hasErrors() || !passwordChangeDto.getConfirmNewPassword().equals(passwordChangeDto.getNewPassword())) {
			return "error/javaScriptValidationIgnored";
		}
		
		Employee employeeEntity = employeeDao.findByEmail(user.getName());
		
		if (employeeEntity.getId() == 1) {
			return "error/adminUserUpdateError";
		}
		
		if (passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), employeeEntity.getPassword()) == false) {
			model.addAttribute("passwordChangeDto", passwordChangeDto);
			
			model.addAttribute("passwordNotCorrect", true);
			
			return "user/changePassword";
		}
		
		employeeEntity.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
		
		employeeDao.save(employeeEntity);
		
		return "redirect:/";
	}
	
	@GetMapping("/tasks")
	public String renderTasksPage(Model model) {
		// TODO
		return "user/tasks";
	}
	
}
