package net.javaci.bank.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.javaci.bank.api.jwt.JwtTokenVerifierFilter;
import net.javaci.bank.api.jwt.JwtUserPassAuthFilter;
import net.javaci.bank.api.service.ApplicationUserService;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

	private final PasswordEncoder passwordEncoder;
	private final ApplicationUserService applicationUserService;
	private final JwtConfig jwtConfig;

	@Autowired
	public ApiSecurityConfig(PasswordEncoder passwordEncoder, ApplicationUserService applicationUserService,
			JwtConfig jwtConfig) {
		this.passwordEncoder = passwordEncoder;
		this.applicationUserService = applicationUserService;
		this.jwtConfig = jwtConfig;
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(new JwtUserPassAuthFilter(authenticationManager(), jwtConfig))
            .addFilterAfter(new JwtTokenVerifierFilter(jwtConfig), JwtUserPassAuthFilter.class)
            .authorizeRequests()
            .antMatchers("/",
                    "/index.html",
                    "/v2/api-docs",
                    "/**.js", 
                    "/**.css", 
                    "/swagger-resources/**",
                    "/swagger-ui.html**",
                    "/webjars/**",
                    "favicon.ico",
                    "/h2-console/**",
                    "/actuator/**").permitAll()
            .anyRequest()
            .authenticated();
        http.cors();
    }

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder);
		provider.setUserDetailsService(applicationUserService);
		auth.authenticationProvider(provider);
	}

}