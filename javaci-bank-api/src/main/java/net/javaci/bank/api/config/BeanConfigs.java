package net.javaci.bank.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.javaci.bank.util.AccountNumberGenerator;

@Configuration
public class BeanConfigs {
	
	@Bean
	public AccountNumberGenerator accountNumberGenerator() {
		return new AccountNumberGenerator();
	}

}
