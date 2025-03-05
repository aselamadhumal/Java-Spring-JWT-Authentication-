package com.jwtauth.jwtauth.controller;

import com.jwtauth.jwtauth.dto.*;
import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.repository.UserRepository;
import com.jwtauth.jwtauth.service.AuthService;
import com.jwtauth.jwtauth.service.JWTService;
import com.jwtauth.jwtauth.service.UserPaginationService;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final UserPaginationService userPaginationService;


    // Fixed: Single constructor with proper field initialization
    public AuthController(AuthService authService, JWTService jwtService, UserRepository userRepository, UserPaginationService userPaginationService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userPaginationService = userPaginationService;
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
        try {
            String refreshRequestId = UUID.randomUUID().toString();
            logger.info("refid: {}", refreshRequestId);

            logger.info("Refresh token request received with ID: {}", refreshRequestId);

            logger.info("Refresh token request received");
            String refreshToken = refreshRequest.getRefreshToken();

            if (refreshToken == null || refreshToken.isEmpty()) {
                logger.warn("Empty refresh token provided");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO("AUTH-003", "Refresh token is required"));
            }


            logger.debug("Validating refresh token: {}", refreshToken);
            String username = jwtService.extractUsername(refreshToken); // Updated method name
            logger.info("Extracted username from refresh token: {}", username);

            if (username != null && jwtService.isTokenValid(refreshToken, username)) {
                Optional<UserEntity> userEntity = userRepository.findByUsername(username);

                if (userEntity.isEmpty()) {
                    logger.info("No user found with this username{}", username);
                    return null;
                }

                UserEntity user = userEntity.get();
                user.setReferencesID(refreshRequestId);
                userRepository.save(user);

                Map<String, Object> claims = new HashMap<>();
                claims.put("username", username);
                claims.put("reference", refreshRequestId);

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




            logger.warn("Invalid refresh token attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("AUTH-002", "Invalid refresh token"));
        } catch (ExpiredJwtException e) {
            logger.error("Refresh token expired: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("AUTH-004", "Refresh token expired"));
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("AUTH-004", "Token refresh failed"));
        }
    }

    /*@PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        // Remove the 'Bearer ' prefix if it exists
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        tokenBlacklistService.blacklistToken(token);
        return ResponseEntity.ok("Successfully logged out and token blacklisted");
    }*/

    /*@PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization")  String referenceId,String token) {
        if (referenceId != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }


        // Add logic to handle token invalidation or blacklisting
        // Example: Save token to a blacklist or remove from a valid session store
        // You might also want to set the user's reference ID to null or update their session status

        return ResponseEntity.ok("Successfully logged out and token blacklisted");
    }*/



    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequestDTO logoutRequestDTO) {
        try {
            // Call the AuthService to process the logout
            authService.logout(logoutRequestDTO.getAccessToken());

            // Return a success response if no exception is thrown
            return ResponseEntity.ok("Successfully logged out");
        } catch (RuntimeException e) {
            // Return a failure response with the error message if an exception occurs
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{field}")
    public APIResponseDTO<List<UserEntity>> getUsersWithSort(@PathVariable String field) {
        List<UserEntity> allUsers = userPaginationService.findUserWithSorting(field);
        return new APIResponseDTO<>(allUsers.size(), allUsers);
    }

    @GetMapping("/pagination")
    public APIResponseDTO<Page<UserEntity>> getUsersWithPagination(
            @RequestParam String offset,
            @RequestParam String pageSize) {
        int of = Integer.parseInt(offset);
        int page = Integer.parseInt(pageSize);
        logger.info("Pagination request received - Offset: {}, PageSize: {}", of, page);
        Page<UserEntity> usersWithPagination = userPaginationService.findUsersWithPagination(of, page);
        logger.info("Pagination response - Total elements in current page: {}, Total elements overall: {}",
                usersWithPagination.getNumberOfElements(),
                usersWithPagination.getTotalElements());
        return new APIResponseDTO<>(usersWithPagination.getNumberOfElements(), usersWithPagination);
    }

    @GetMapping("/paginationAndSort/{offset}/{pageSize}/{field}")
    public APIResponseDTO<Page<UserEntity>> getUsersWithPaginationAndSort(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field) {
        Page<UserEntity> usersWithPagination = userPaginationService.findUsersWithPaginationAndSorting(offset, pageSize, field);
        return new APIResponseDTO<>(usersWithPagination.getNumberOfElements(), usersWithPagination);
    }








}