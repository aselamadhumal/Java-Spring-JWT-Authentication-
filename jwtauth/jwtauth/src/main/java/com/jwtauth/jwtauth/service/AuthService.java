package com.jwtauth.jwtauth.service;

import com.jwtauth.jwtauth.dto.LoginRequestDTO;
import com.jwtauth.jwtauth.dto.LoginResponseDTO;
import com.jwtauth.jwtauth.dto.RegisterRequestDTO;
import com.jwtauth.jwtauth.dto.RegisterResponseDTO;
import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            logger.error("Authentication failed for user: {}", loginData.getUsername(), e);
            return new LoginResponseDTO(null, null, "Invalid credentials", "The username or password is incorrect.");
        } catch (Exception e) {
            logger.error("Unexpected error during authentication. {}",e.getLocalizedMessage(), e);
            return new LoginResponseDTO(null, null, "Authentication failed", "An unexpected error occurred during authentication.");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "user");
        claims.put("email", "asela@gmail.com");

        String token = jwtService.getJWTToken(loginData.getUsername(), claims);
        logger.info("Token generated for user: {}", loginData.getUsername());
        return new LoginResponseDTO(token, LocalDateTime.now(), null, "Token generated successfully");
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
