package net.javaci.bank.backoffice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@GetMapping("/tasks")
	public String renderTasksPage(Model model) {
		return "user/tasks";
	}
	
	@GetMapping("/profile")
	public String renderProfilePage(Model model) {
		return "user/profile";
	}
	
	@GetMapping("/changePassword")
	public String renderChangePasswordPage(Model model) {
		return "user/changePassword";
	}
}
