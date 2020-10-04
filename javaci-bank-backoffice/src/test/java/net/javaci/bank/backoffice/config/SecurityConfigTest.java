package net.javaci.bank.backoffice.config;

import net.javaci.bank.backoffice.controller.AccountController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SecurityConfigTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final String local() {
        return "http://localhost:" + port;
    }

    @Test
    public void cssShouldHaveAnonymousAccess() throws Exception {
        // Given
        // When
        String content = restTemplate.getForObject(local() + "/css/styles.css",
                String.class);
        // Then
        assertTrue(content.contains("Additional Note: Customizations done by javaci.net"), "css files should have anonymous acces");
    }

    @Test
    public void  jsShouldHaveAnonymousAccess() throws Exception {
        // Given
        // When
        String content = restTemplate.getForObject(local() + "/js/scripts.js",
                String.class);
        // Then
        assertTrue(content.contains("Additional Note: Customizations done by javaci.net"), "js files should have anonymous acces");
    }

    // TODO: Koray Duzelt. SecurityConfig de img dizini anonim degil ama erisiliyor. Nasil test edecegim?
    //@Test
    public void  imgShouldHaveAnonymousAccess() throws Exception {
        try {
            byte[] imageBytes = restTemplate.getForObject(local() + "/img/turkey-flag-16.png", byte[].class);
            assertNotNull(imageBytes, "image could not read");
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            int height = image.getHeight();
            int width = image.getWidth();
            assertEquals(height, 16);
            assertEquals(width, 16);
        } catch (IOException e) {
            fail("Could not access to anonym image");
        }

    }

    @Test
    public void loginRequiredWithoutAuthentication() throws Exception {
        // Given
        // When
        String content = restTemplate.getForObject(local() + AccountController.BASE_URL + AccountController.LIST_URL,
                String.class);
        // Then
        assertTrue(content.contains("id=\"username\" name=\"username\""), "API cannot be accessable without authentication");
    }
}