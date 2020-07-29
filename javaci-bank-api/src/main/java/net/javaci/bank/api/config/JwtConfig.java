package net.javaci.bank.api.config;


import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
@ConfigurationProperties(prefix = "application.jwt")
@Getter @Setter
public class JwtConfig {

    private String secretKey;

    private Integer tokenExpirationAfterDays;

    // Jwt key olusturma algoritmasini birden fazla yerde kullandigimiz icin bu sekilde methoda aldik
    public SecretKey createSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
