package net.javaci.bank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class JavaciBankApiAppTest {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Test
	public void contextLoads() {
		
	}
	
	@Test
	public void testPasswordEncoding() {
		
		String expected = "$2a$10$9To1UkYzdBfF0fG16pbirOu6BayXeCAJ0iHol1ivPppbMpBR0/uAy";
		String rawPassword = "abcd.1234";
		String encoded = passwordEncoder.encode(rawPassword);
		System.out.println(encoded);
	}
}
