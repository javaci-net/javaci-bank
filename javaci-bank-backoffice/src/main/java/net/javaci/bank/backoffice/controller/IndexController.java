package net.javaci.bank.backoffice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping(value = {"/", "index"})
    public String homePage(Model model) {
        model.addAttribute("welcomeMsg", "Welcome To Javaci-Bank Backoffice");
        return "index";
    }
}
