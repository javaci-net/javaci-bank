package net.javaci.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication(scanBasePackages = {"net.javaci.bank.db.dao", "net.javaci.bank.backoffice"})
public class JavaciBankBackofficeApp {

    public static void main(String[] args) {
        try {
        	SpringApplication.run(JavaciBankBackofficeApp.class, args);
        } catch (Throwable e) {
            if(e.getClass().getName().contains("SilentExitException")) {
            	// skipping for spring known bug https://github.com/spring-projects/spring-boot/issues/3100
                log.debug("Spring is restarting the main thread - See spring-boot-devtools");
            } else {
                throw e;
            }
        }
        
    }

}
