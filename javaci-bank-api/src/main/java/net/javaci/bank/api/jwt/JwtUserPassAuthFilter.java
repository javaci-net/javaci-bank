package net.javaci.bank.api.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import net.javaci.bank.api.config.JwtConfig;
import net.javaci.bank.api.helper.JwtConstants;
import net.javaci.bank.api.model.UserPassAuthRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

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
        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
                .signWith(jwtConfig.createSecretKey())
                .compact();

        response.addHeader(JwtConstants.AUTHORIZATION, JwtConstants.BEARER_PREFIX + " " + token);
        logger.info(String.format("Jwt token successfully added to header for user: %s" , authResult.getName()));
    }
}
