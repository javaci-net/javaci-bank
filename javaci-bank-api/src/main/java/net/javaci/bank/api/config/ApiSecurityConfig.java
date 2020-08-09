package net.javaci.bank.api.config;

import net.javaci.bank.api.jwt.JwtTokenVerifierFilter;
import net.javaci.bank.api.jwt.JwtUserPassAuthFilter;
import net.javaci.bank.api.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;
    private final JwtConfig jwtConfig;
    
    // FIXME Korayla bakilacak
    private static final boolean ENABLE_SECURTY = false; 

    @Autowired
    public ApiSecurityConfig(PasswordEncoder passwordEncoder,
                             ApplicationUserService applicationUserService,
                             JwtConfig jwtConfig) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	if (ENABLE_SECURTY) {
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
	                        "/swagger-resources/**",
	                        "/swagger-ui.html**",
	                        "/webjars/**",
	                        "favicon.ico",
	                        "/h2-console/**",
	                        "/actuator/**").permitAll()
	                //.antMatchers("/api/**").hasRole("USER")
	                .anyRequest()
	                .authenticated();
    	}
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    //@Override
    public void configure2(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/",
                "/index.html",
                "/swagger/**",
                "/swagger/v2/**",
                "/h2-console/**",
                "/actuator/**"
        );
    }


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }

}