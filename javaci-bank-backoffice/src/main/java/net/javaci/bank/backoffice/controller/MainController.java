package net.javaci.bank.backoffice.controller;

import java.security.Principal;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String root(Model model) {
        model.addAttribute("welcomeMsgFromBean", "Welcome To Javaci-Bank Backoffice");
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model, Principal user) {
    	
    	if (user != null) {
            return "redirect:/";
        }
    	
        return "login";
    }
    
}
