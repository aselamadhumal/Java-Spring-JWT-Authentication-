package com.jwtauth.jwtauth.service;

import com.jwtauth.jwtauth.dto.*;
import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public List<UserEntity> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll();
    }

    public UserEntity createUser(RegisterRequestDTO userData) {
        logger.info("Creating user with username: {}", userData.getUsername());
        UserEntity newUser = new UserEntity(
                userData.getUsername(),
                userData.getEmail(),
                passwordEncoder.encode(userData.getPassword())
        );
        UserEntity savedUser = userRepository.save(newUser);
        logger.info("User created with ID: {}", savedUser.getId());
        return savedUser;
    }

    public LoginResponseDTO login(LoginRequestDTO loginData) {
        logger.info("Attempting login for user: {}", loginData.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword())
            );
        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for user: {} - Invalid credentials", loginData.getUsername());
            return new LoginResponseDTO(null, null, "Invalid credentials", "The username or password is incorrect.", null);
        } catch (Exception e) {
            logger.error("Unexpected error during authentication for user {}: {}", loginData.getUsername(), e.getMessage(), e);
            return new LoginResponseDTO(null, null, "Authentication failed", "An unexpected error occurred during authentication.", null);
        }

        UUID loginAttemptId = UUID.randomUUID();
        logger.info("Login attempt ID: {}", loginAttemptId);


        Map<String, Object> claims = new HashMap<>();
        claims.put("username", loginData.getUsername());

        String accessToken = jwtService.getJWTToken(loginData.getUsername(), claims);
        String refreshToken = jwtService.getRefreshToken(loginData.getUsername(), claims);

        logger.info("Token generated successfully for user: {}", loginData.getUsername());
        return new LoginResponseDTO(accessToken, LocalDateTime.now(), null, "Token generated successfully", refreshToken);
    }

    public RegisterResponseDTO register(RegisterRequestDTO registerData) {
        logger.info("Registering user with username: {}", registerData.getUsername());

        if (isUserEnabled(registerData.getUsername())) {
            logger.warn("User already exists: {}", registerData.getUsername());
            return new RegisterResponseDTO(null, "User already exists in the system");
        }

        try {
            UserEntity userData = this.createUser(registerData);
            logger.info("User registered successfully with ID: {}", userData.getId());
            return new RegisterResponseDTO(String.format("User registered with ID %s", userData.getId()), null);
        } catch (Exception e) {
            logger.error("System error occurred while registering user: {} - {}", registerData.getUsername(), e.getMessage(), e);
            return new RegisterResponseDTO(null, "System error during registration");
        }
    }








    private boolean isUserEnabled(String username) {
        boolean isEnabled = userRepository.findByUsername(username).isPresent();
        logger.info("Is user '{}' enabled: {}", username, isEnabled);
        return isEnabled;
    }
}