package com.jwtauth.jwtauth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class JWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

//    private final SecretKey secretKey;

    @Value("${jwt.secret}")
    private String jwtSecret;


    public JWTService() {
        /*try {
            logger.info("Initializing JWTService and generating secret key...");
            SecretKey k = KeyGenerator.getInstance("HmacSHA256").generateKey();
            secretKey = Keys.hmacShaKeyFor(k.getEncoded());
            logger.info("Secret key successfully generated.");
        } catch (Exception e) {
            logger.error("Error generating secret key for JWTService", e);
            throw new RuntimeException(e);
        }*/
    }

    public String getJWTToken(String username, Map<String, Object> claims) {
        logger.info("Generating JWT token for user: {}", username);

        byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
        SecretKey secretKey = new SecretKeySpec(decodedKey, "HmacSHA256");


        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 minutes expiration
                .signWith(secretKey)
                .compact();

        logger.info("JWT token generated successfully for user: {}", username);
        return token;
    }

    public String getRefreshToken(String username, Map<String, Object> claims) {
        logger.info("Generating refresh token for user: {}", username);

        byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
        SecretKey secretKey = new SecretKeySpec(decodedKey, "HmacSHA256");

        String refreshToken = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiration
                .signWith(secretKey)
                .compact();

        logger.info("Refresh token generated successfully for user: {}", username);
        return refreshToken;
    }

    // Get the username from the token
    public String getUserName(String token) {
        Claims data = getTokenData(token);
        if (data == null) return null;
        return data.getSubject();
    }

    // Get specific field from the token
    public Object getFieldFromToken(String token, String key) {
        Claims data = getTokenData(token);
        if (data == null) return null;
        return data.get(key);
    }

    // Parse the token and extract claims using parserBuilder()
    public Claims getTokenData(String token) {
        byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
        SecretKey secretKey = new SecretKeySpec(decodedKey, "HmacSHA256");

        try {
            // Parsing JWT token using Jwts.parserBuilder()
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            logger.error("Error parsing or validating token", e);
            return null; // Token is invalid or expired
        }
    }

    // Validate the token and check if it's not expired
    public boolean isTokenValid(String token, String username) {
        String tokenUsername = getUserName(token);
        return tokenUsername != null && tokenUsername.equals(username) && !isTokenExpired(token);
    }

    // Check if the token is expired
    public boolean isTokenExpired(String token) {
        Claims claims = getTokenData(token);
        if (claims == null) return true; // Token is invalid
        return claims.getExpiration().before(new Date()); // Check if the token is expired
    }



    // Extract username from the token (this method can be simplified)
    public String extractUsername(String token) {
        Claims claims = getTokenData(token);
        if (claims != null) {
            return claims.getSubject(); // The subject of the JWT is the username
        }
        return null;
    }


    /*public Date getExpiresAt(String token) {
        Claims claims = getTokenData(token);
        if (claims != null) {
            return claims.getExpiration(); // Get the expiration date from claims
        }
        return null;
    }*/
}
