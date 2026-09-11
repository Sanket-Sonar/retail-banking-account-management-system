package com.bank.system.management.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "12345678901234567890123456789012345678901234567890";

    private SecretKey getSignInKey() {

        byte[] keyBytes = SECRET_KEY.getBytes();

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email) {

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis() + 3600000))
                .signWith(getSignInKey())
                .compact();
    }

    public String extractEmail(String token) {

        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        try {
            String email = extractEmail(token);

            return email.equals(userDetails.getUsername())
                    && !isTokenExpired(token);

        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {

        Date expiryDate =
                Jwts.parser()
                        .verifyWith(getSignInKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getExpiration();

        return expiryDate.before(new Date());
    }

}
