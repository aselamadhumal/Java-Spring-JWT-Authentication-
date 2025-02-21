
package com.jwtauth.jwtauth.controller;

import com.jwtauth.jwtauth.dto.*;
import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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

    @PostMapping("/refreshToken")
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        logger.info("Refresh token request received.");
        LoginResponseDTO response = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(response);
    }
}
