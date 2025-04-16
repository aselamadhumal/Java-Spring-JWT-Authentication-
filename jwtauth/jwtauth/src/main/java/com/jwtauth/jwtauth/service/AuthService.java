package com.jwtauth.jwtauth.service;

import com.jwtauth.jwtauth.dto.*;
import com.jwtauth.jwtauth.exceptions.AuthenticationException;
import com.jwtauth.jwtauth.exceptions.LoginFailedException;
import com.jwtauth.jwtauth.exceptions.UserNotFoundException;
import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

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
        UserEntity newUser = new UserEntity();
        newUser.setUsername(userData.getUsername());
        newUser.setPassword(passwordEncoder.encode(userData.getPassword()));
        newUser.setEmail(userData.getEmail());

        UserEntity savedUser = userRepository.save(newUser);
        logger.info("User created with ID: {}", savedUser.getId());
        return savedUser;
    }


   /* public LoginResponseDTO login(LoginRequestDTO loginData) {
        // Generate a unique identifier for this login attempt
        String referencesID = UUID.randomUUID().toString();



        logger.info("ref: {}", referencesID);

        // Existing login logic
        logger.info("Attempting login for user: {}", loginData.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword())
            );
        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for user: {} - In" +
                    "valid credentials", loginData.getUsername());
            return new LoginResponseDTO(null, null, "Invalid credentials", "The username or password is incorrect.", null);
        } catch (Exception e) {
            logger.error("Unexpected error during authentication for user {}: {}", loginData.getUsername(), e.getMessage(), e);
            return new LoginResponseDTO(null, null, "Authentication failed", "An unexpected error occurred during authentication.", null);
        }

        // Token generation logic
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", loginData.getUsername());
        claims.put("reference", referencesID);

        String accessToken = jwtService.getJWTToken(loginData.getUsername(), claims);
        String refreshToken = jwtService.getRefreshToken(loginData.getUsername(), claims);

        Optional<UserEntity> userEntity = userRepository.findByUsername(loginData.getUsername());

        if (userEntity.isEmpty()) {
            logger.info("No user found with this username{}", loginData.getUsername());
            return null;
        }

        UserEntity user = userEntity.get();
        user.setReferencesID(referencesID);
        user.setExpireAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        logger.info("Token generated successfully for user: {}", loginData.getUsername());
        return new LoginResponseDTO(accessToken, LocalDateTime.now(), null, "Token generated successfully", refreshToken);
    }*/

    public LoginResponseDTO login(LoginRequestDTO loginData) {
        String referencesID = UUID.randomUUID().toString();
        logger.info("ref: {}", referencesID);


        logger.info("Attempting login for user: {}", loginData.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword())
            );
        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for user: {} - Invalid credentials", loginData.getUsername());
            //throw new LoginFailedException("Invalid credentials: The username or password is incorrect.");
            throw new LoginFailedException(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during authentication for user {}: {}", loginData.getUsername(), e.getMessage(), e);
            throw new AuthenticationException("An unexpected error occurred during authentication.");
        }

        // Token generation logic
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", loginData.getUsername());
        claims.put("reference", referencesID);

        String accessToken = jwtService.getJWTToken(loginData.getUsername(), claims);
        String refreshToken = jwtService.getRefreshToken(loginData.getUsername(), claims);

        Optional<UserEntity> userEntity = userRepository.findByUsername(loginData.getUsername());

        if (userEntity.isEmpty()) {
            logger.info("No user found with this username {}", loginData.getUsername());
            throw new UserNotFoundException("User not found with username: " + loginData.getUsername());
        }

        UserEntity user = userEntity.get();
        user.setReferencesID(referencesID);
        user.setExpireAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

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

    /*public LogoutRequestDTO logout(String accessToken) {

        Claims claims =jwtService.getTokenData(accessToken);
        String reference = claims.get("reference").toString();
        String username = claims.getSubject();

        if (!StringUtils.hasLength(reference)) {
            logger.warn("No reference ID found in the provided token.");
            return new LogoutRequestDTO("Invalid token or reference ID not found", null);
        }

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);

        if (userEntity.isEmpty()) {
            logger.warn("No user found with reference ID: {}", reference);
            return new LogoutRequestDTO("User not found or already logged out", null);
        }

        UserEntity user = userEntity.get();


        user.setReferencesID(null);
        userRepository.save(user);

        logger.info("User with reference ID: {} has been successfully logged out.", reference);

        // Optional: If you are maintaining a session store (like Redis), you can also remove the session here.
        // sessionStore.remove(referencesID);

        return new LogoutRequestDTO("Successfully logged out", null);
    }*/



    public void logout(String accessToken) {

        Claims claims = jwtService.getTokenData(accessToken);
        String reference = claims.get("reference").toString();
        String username = claims.getSubject();

        if (!StringUtils.hasLength(reference)) {
            logger.warn("No reference ID found in the provided token.");
            throw new RuntimeException("Invalid token or reference ID not found");
        }

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);

        if (userEntity.isEmpty()) {
            logger.warn("No user found with reference ID: {}", reference);
            throw new RuntimeException("User not found or already logged out");
        }





        UserEntity user = userEntity.get();
        user.setReferencesID(null);

        userRepository.save(user);

        logger.info("User with reference ID: {} has been successfully logged out.", reference);

        // Optional: If you are maintaining a session store (like Redis), you can also remove the session here.
        // sessionStore.remove(referencesID);
    }


    private boolean isUserEnabled(String username) {
        boolean isEnabled = userRepository.findByUsername(username).isPresent();
        logger.info("Is user '{}' enabled: {}", username, isEnabled);
        return isEnabled;
    }


    

}