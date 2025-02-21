package com.jwtauth.jwtauth.service;

import com.jwtauth.jwtauth.dto.*;
import com.jwtauth.jwtauth.model.RefreshToken;
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
import java.util.Optional;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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
            logger.error("Authentication failed for user: {}", loginData.getUsername(), e);
            return new LoginResponseDTO(null, null, "Invalid credentials", "The username or password is incorrect.", "null");
        } catch (Exception e) {
            logger.error("Unexpected error during authentication: {}", e.getLocalizedMessage(), e);
            return new LoginResponseDTO(null, null, "Authentication failed", "An unexpected error occurred during authentication.", "null");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", loginData.getUsername());

        String token = jwtService.getJWTToken(loginData.getUsername(), claims);
        logger.info("Token generated for user: {}", loginData.getUsername());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginData.getUsername());

        return new LoginResponseDTO(token, LocalDateTime.now(), null, "Token generated successfully", refreshToken.getReToken());
    }

    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequest) {
        logger.info("Processing refresh token request.");

        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByReToken(refreshTokenRequest.getReToken());

        if (refreshTokenOpt.isEmpty()) {
            logger.warn("Refresh token not found in database.");
            return new LoginResponseDTO(null, null, "Invalid refresh token", "Refresh token does not exist or is expired.", null);
        }

        try {
            // Verify and invalidate the old refresh token
            RefreshToken oldRefreshToken = refreshTokenOpt.get();
            refreshTokenService.deleteRefreshToken(oldRefreshToken);

            // Retrieve user entity
            UserEntity userEntity = oldRefreshToken.getUserEntity();

            // Generate a new refresh token
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(userEntity.getUsername());

            // Generate a new access token
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", userEntity.getUsername());
            String newAccessToken = jwtService.getJWTToken(userEntity.getUsername(), claims);

            logger.info("New access & refresh tokens generated for user: {}", userEntity.getUsername());

            return new LoginResponseDTO(newAccessToken, LocalDateTime.now(), null, "New tokens issued", newRefreshToken.getReToken());

        } catch (Exception e) {
            logger.error("Error while processing refresh token: {}", e.getMessage());
            return new LoginResponseDTO(null, null, "Token refresh failed", "An error occurred while refreshing token.", null);
        }
    }


    public RegisterResponseDTO register(RegisterRequestDTO registerData) {
        logger.info("Registering user with username: {}", registerData.getUsername());

        if (isUserEnabled(registerData.getUsername())) {
            logger.warn("User already exists: {}", registerData.getUsername());
            return new RegisterResponseDTO(null, "User already exists in the system");
        }

        UserEntity userData = this.createUser(registerData);
        if (userData.getId() == null) {
            logger.error("System error occurred while registering user: {}", registerData.getUsername());
            return new RegisterResponseDTO(null, "System error");
        }

        logger.info("User registered successfully with ID: {}", userData.getId());
        return new RegisterResponseDTO(String.format("User registered with ID %s", userData.getId()), null);
    }

    private boolean isUserEnabled(String username) {
        boolean isEnabled = userRepository.findByUsername(username).isPresent();
        logger.info("Is user '{}' enabled: {}", username, isEnabled);
        return isEnabled;
    }
}