package net.javaci.bank;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class JavaciBankApiApp implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(JavaciBankApiApp.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.debug("Starting Javaci Bank Api App");
	}
	
	@Bean
	public ModelMapper modelMapper() {
	    return new ModelMapper();
	}
}
