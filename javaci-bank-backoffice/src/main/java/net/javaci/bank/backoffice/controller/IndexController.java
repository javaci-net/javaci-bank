package net.javaci.bank.backoffice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class IndexController {

    //@GetMapping(value = {"/index"})
    @GetMapping("/index")
    public String homePage(Model model) {
        model.addAttribute("welcomeMsg", "Welcome To Javaci-Bank Backoffice");
        return "employee/index";
    }
}
