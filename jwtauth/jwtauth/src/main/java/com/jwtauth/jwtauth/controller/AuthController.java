package com.jwtauth.jwtauth.controller;

import com.jwtauth.jwtauth.dto.*;
import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.service.AuthService;
import com.jwtauth.jwtauth.service.JWTService;
import com.jwtauth.jwtauth.service.TokenBlacklistService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final JWTService jwtService;

    // Fixed: Single constructor with proper field initialization
    public AuthController(AuthService authService, JWTService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @GetMapping("/list")
    public List<UserEntity> getAllUsers() {
        return authService.getAllUsers();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginData) {
        logger.info("Login attempt for user: {}", loginData.getUsername());
        LoginResponseDTO res = authService.login(loginData);

        if (res.getError() != null) {
            logger.warn("Login failed for user: {}. Reason: {}", loginData.getUsername(), res.getError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        logger.info("Login successful for user: {}", loginData.getUsername());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO registerData) {
        logger.info("Registration attempt for user: {}", registerData.getUsername());
        RegisterResponseDTO res = authService.register(registerData);

        if (res.getError() != null) {
            logger.warn("Registration failed for user: {}. Reason: {}", registerData.getUsername(), res.getError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        logger.info("Registration successful for user: {}", registerData.getUsername());
        return ResponseEntity.ok(res);
    }

    // Fixed: Changed to use RequestBody and added proper error handling
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequestDTO refreshRequest) {
        logger.info("Refresh token request received");
        String refreshToken = refreshRequest.getRefreshToken();

        if (refreshToken == null || refreshToken.isEmpty()) {
            logger.warn("Empty refresh token provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("AUTH-003", "Refresh token is required"));
        }

        try {
            logger.debug("Validating refresh token: {}", refreshToken);
            String username = jwtService.extractUsername(refreshToken); // Updated method name
            logger.info("Extracted username from refresh token: {}", username);

            if (username != null && jwtService.isTokenValid(refreshToken, username)) {
                Map<String, Object> claims = new HashMap<>();
                claims.put("username", username);

                String newAccessToken = jwtService.getJWTToken(username, claims);
                String newRefreshToken = jwtService.getRefreshToken(username, claims); // Implement token rotation

                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", newAccessToken);
                tokens.put("refreshToken", newRefreshToken);

                logger.info("Tokens refreshed successfully for user: {}", username);
                return ResponseEntity.ok(tokens);
            } else {
                logger.warn("Token validation failed for user: {}", username);
            }
        } catch (ExpiredJwtException e) {
            logger.error("Refresh token expired: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("AUTH-004", "Refresh token expired"));
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage(), e);
        }

        logger.warn("Invalid refresh token attempt");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO("AUTH-002", "Invalid refresh token"));
    }


    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    /*@PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        // Remove the 'Bearer ' prefix if it exists
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        tokenBlacklistService.blacklistToken(token);
        return ResponseEntity.ok("Successfully logged out and token blacklisted");
    }*/

    @PostMapping("/logout")
    public String logout(@RequestBody LogoutRequestDTO logoutRequest) {
        tokenBlacklistService.blacklistToken(logoutRequest.getAccessToken());
        tokenBlacklistService.blacklistToken(logoutRequest.getRefreshToken());
        return "Logged out successfully";
    }






}