package com.bank.system.management.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET_KEY =
            "12345678901234567890123456789012345678901234567890";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void generateToken_ShouldReturnValidToken() {

        String email = "test@gmail.com";

        String token = jwtService.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractEmail_ShouldReturnCorrectEmail() {

        String email = "test@gmail.com";

        String token = jwtService.generateToken(email);

        String extractedEmail = jwtService.extractEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenBelongsToUser() {

        String email = "test@gmail.com";

        String token = jwtService.generateToken(email);

        UserDetails userDetails =
                new User(
                        email,
                        "password",
                        Collections.emptyList());

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameDoesNotMatch() {

        String token =
                jwtService.generateToken("test@gmail.com");

        UserDetails userDetails =
                new User(
                        "other@gmail.com",
                        "password",
                        Collections.emptyList());

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenExpired() {

        SecretKey key =
                Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        String expiredToken =
                Jwts.builder()
                        .subject("test@gmail.com")
                        .issuedAt(
                                new Date(
                                        System.currentTimeMillis() - 7200000))
                        .expiration(
                                new Date(
                                        System.currentTimeMillis() - 3600000))
                        .signWith(key)
                        .compact();

        UserDetails userDetails =
                new User(
                        "test@gmail.com",
                        "password",
                        Collections.emptyList());

        assertFalse(
                jwtService.isTokenValid(
                        expiredToken,
                        userDetails));
    }
}