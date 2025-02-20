package com.jwtauth.jwtauth.controller;

import com.jwtauth.jwtauth.dto.*;
import com.jwtauth.jwtauth.model.RefreshToken;
import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.service.AuthService;
import com.jwtauth.jwtauth.service.JWTService;
import com.jwtauth.jwtauth.service.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;


    public AuthController(AuthService authService, JWTService jwtService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    // Get all users
    @GetMapping("/list")
    public List<UserEntity> getAllUsers() {
        return authService.getAllUsers();
    }

    // Create a new user
    /*@PostMapping("/register")
    public UserEntity createUser(@RequestBody RegisterRequestDTO user) {
        return authService.createUser(user);
    }*/

   /* @PostMapping("/login")
    public String login(@RequestBody UserEntity user) {
        return jwtService.getJWTToken(user.getUsername());
    }*/

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO>login(@RequestBody LoginRequestDTO loginData) {

        logger.info("Login attempt for user: {}", loginData.getUsername());

        LoginResponseDTO res = authService.login(loginData);
        if(res.getError() != null) {

            logger.warn("Login failed for user: {}. Reason: {}", loginData.getUsername(), res.getError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        logger.info("Login successful for user: {}", loginData.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO>register(@RequestBody RegisterRequestDTO registerData) {

        logger.info("Registration attempt for user: {}", registerData.getUsername());

        RegisterResponseDTO res = authService.register(registerData);

        if(res.getError() != null) {

            logger.warn("Registration failed for user: {}. Reason: {}", registerData.getUsername(), res.getError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        }
        logger.info("Registration successful for user: {}", registerData.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }



    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        return refreshTokenService.findByReToken(refreshTokenRequest.getReToken())
                .map(refreshTokenService::verifyRefreshToken)
                .map(RefreshToken::getUserEntity)
                .map(userEntity -> {
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("username", userEntity.getUsername());
                    claims.put("email", userEntity.getEmail());
                    String token = jwtService.getJWTToken(userEntity.getUsername(), claims);
                    return ResponseEntity.ok(LoginResponseDTO.builder()
                            .token(token)
                            .reToken(refreshTokenRequest.getReToken())
                            .build());
                }).orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }






}
