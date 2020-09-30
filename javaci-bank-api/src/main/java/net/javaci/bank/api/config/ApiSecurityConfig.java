package net.javaci.bank.api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import net.javaci.bank.api.jwt.JwtTokenVerifierFilter;
import net.javaci.bank.api.jwt.JwtUserPassAuthFilter;
import net.javaci.bank.api.jwt.JwtUserPassAuthFilterNoCheck;
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
        	// added to allow h2-console frames
        	.headers().addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN)).and()
        	
        	// by default uses a Bean by the name of corsConfigurationSource
            .csrf().disable().cors().and() 
            
            // session configs - stateless
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            
            // jwt authorization filters
            .addFilter(addJwtAuthorizationFilter())
            .addFilterAfter(new JwtTokenVerifierFilter(jwtConfig), JwtUserPassAuthFilter.class)
            .authorizeRequests()
            
            // set allowed urls (no security)
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
                    "/actuator/**",
                    "/api/customer/register").permitAll()
            .anyRequest()
            .authenticated();
    }

	private JwtUserPassAuthFilter addJwtAuthorizationFilter() throws Exception {
		if (jwtConfig.getAuthtype() != null && "none".equals(jwtConfig.getAuthtype())) {
			return new JwtUserPassAuthFilterNoCheck(authenticationManager(), jwtConfig);
		}
		return new JwtUserPassAuthFilter(authenticationManager(), jwtConfig);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder);
		provider.setUserDetailsService(applicationUserService);
		auth.authenticationProvider(provider);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		// CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues(); // Kolaylik icin bu da kullanilabilir
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setExposedHeaders(Arrays.asList("authorization","connection"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
