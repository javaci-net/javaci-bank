package net.javaci.bank.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	@Qualifier("customerUserDetailService")
	private UserDetailsService customerDetailService;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// configure AuthenticationManager so that it knows from where to load
		// user for matching credentials
		// Use BCryptPasswordEncoder
		auth.userDetailsService(customerDetailService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		/*
		httpSecurity.csrf().disable()
		.authorizeRequests()
		.antMatchers("/api/**").authenticated()
		/*.anyRequest().permitAll()*//*.and()
		.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
		.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		*/

		httpSecurity.antMatcher("/api/customer/**").authorizeRequests()
			.anyRequest().authenticated()
        	.and()
        	.addFilterBefore(new CustomerRequestFilter(jwtTokenUtil, customerDetailService), UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint);

		// Add a filter to validate the tokens with every request
		// httpSecurity.antMatcher("/api/**").addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
