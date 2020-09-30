package net.javaci.bank.api.jwt;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.javaci.bank.api.config.JwtConfig;
import net.javaci.bank.api.model.UserPassAuthRequest;

public class JwtUserPassAuthFilterNoCheck extends JwtUserPassAuthFilter {

	public JwtUserPassAuthFilterNoCheck(AuthenticationManager authenticationManager, JwtConfig jwtConfig) {
		super(authenticationManager, jwtConfig);
	}
	
	@Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) {
        UserPassAuthRequest authReq;
        try {
            authReq = new ObjectMapper()
                    .readValue(request.getInputStream(), UserPassAuthRequest.class);
        } catch (IOException e) {
            throw new BadCredentialsException("Auth request cannot be parsed");
        }

        return new UsernamePasswordAuthenticationToken(authReq.getUsername(), authReq.getPassword());
    }

}
