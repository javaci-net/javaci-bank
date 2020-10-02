package net.javaci.bank.backoffice.controller;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DashboardController {
	
	@Autowired
	private MessageSource messageSource;
	
    @GetMapping("/")
    public String root(Model model, Principal user, Locale locale) {
    	// ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    	// messageSource.setBasenames("lang/res");

    	log.debug(messageSource.getMessage("welcomeMsg", null, Locale.ENGLISH));
    	log.debug(messageSource.getMessage("welcomeMsg", null, new Locale("TR", "TR")));
    	log.debug(messageSource.getMessage("welcomeMsg", null, locale));
    	
        // model.addAttribute("welcomeMsgFromBean", "Welcome To Javaci-Bank Backoffice from Controler");
        // model.addAttribute("welcomeMsgFromMessagesAndBean", messageSource.getMessage("welcomeMsg", null, loc) +  " from Controler");
        
        return "dashboard";
    }
    
}
