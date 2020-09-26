package net.javaci.bank.backoffice.controller;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
	
	@Autowired
	MessageSource messageSource;
	
    @GetMapping("/")
    public String root(Model model, Principal user, Locale loc) {
    	// ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    	// messageSource.setBasenames("lang/res");

    	System.out.println(messageSource.getMessage("welcomeMsg", null, Locale.ENGLISH));
    	System.out.println(messageSource.getMessage("welcomeMsg", null, new Locale("TR", "TR")));
    	System.out.println(messageSource.getMessage("welcomeMsg", null, loc));
    	
        model.addAttribute("welcomeMsgFromBean", "Welcome To Javaci-Bank Backoffice from Controler");
        model.addAttribute("welcomeMsgFromMessagesAndBean", messageSource.getMessage("welcomeMsg", null, loc) +  " from Controler");
        
        return "dashboard";
    }
    
}
