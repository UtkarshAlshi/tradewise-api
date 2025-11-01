package com.tradewise.api.service;

import com.tradewise.api.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
// Import Spring's UserDetails
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("${tradewise.app.jwtSecret}")
    private String jwtSecret;

    @Value("${tradewise.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // Generates a token for a given user
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the email (subject) from a JWT.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from a JWT.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract a specific "claim" (data) from the token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Helper method to parse the token and get all its data.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks if a token is expired.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * The main validation method.
     * --- THIS IS THE CORRECTED SIGNATURE ---
     * It now accepts Spring Security's UserDetails object.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        // We use userDetails.getUsername() because we set the email as the username
        // in UserDetailsServiceImpl
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Helper method to create the signing key.
     * --- THIS IS THE SINGLE, CORRECT METHOD ---
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = this.jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}