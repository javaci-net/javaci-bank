package net.javaci.bank.api.jwt;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import net.javaci.bank.api.config.JwtConfig;
import net.javaci.bank.api.helper.JwtConstants;
import net.javaci.bank.api.model.UserPassAuthRequest;

@Slf4j
/**
 * ust siniftan "/login" adresine post data gonderildiginde Controller gibi calisan filtredir seklinde
 */
public class JwtUserPassAuthFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;

    public JwtUserPassAuthFilter(AuthenticationManager authenticationManager,
                                 JwtConfig jwtConfig) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        logger.info("JwtUserPassAuthFilter created");
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

        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authReq.getUsername(), authReq.getPassword()));

        if (auth.isAuthenticated()) {
            logger.info(String.format("User: %s is authenticated. Roles: %s", authReq.getUsername(), auth.getAuthorities()));
        }
        return auth;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String token = createJwtToken(authResult.getName(), authResult.getAuthorities(), jwtConfig);

        response.addHeader(JwtConstants.AUTHORIZATION, JwtConstants.BEARER_PREFIX + " " + token);
        logger.info(String.format("Jwt token successfully added to header for user: %s" , authResult.getName()));
    }


	public static String createJwtToken(String subject, Collection<? extends GrantedAuthority> collection, JwtConfig jwtConfig) {
		return Jwts.builder()
                .setSubject(subject)
                .claim("authorities", collection)
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
                .signWith(jwtConfig.createSecretKey())
                .compact();
	}
}
