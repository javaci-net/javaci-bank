package net.javaci.bank.backoffice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/transaction")
public class TransactionController {
	
	@GetMapping("/list")
	public String renderListPage(Model model) {
		return "transaction/list";
	}

}
