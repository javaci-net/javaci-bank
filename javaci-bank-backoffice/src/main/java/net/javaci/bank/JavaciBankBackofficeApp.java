package net.javaci.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"net.javaci.bank.db.dao", "net.javaci.bank.backoffice"})
public class JavaciBankBackofficeApp {

    public static void main(String[] args) {
        SpringApplication.run(JavaciBankBackofficeApp.class, args);
    }

}
