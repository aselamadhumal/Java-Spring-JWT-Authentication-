package com.jwtauth.jwtauth.controller;

import com.jwtauth.jwtauth.dto.UserRequestDTO;
import com.jwtauth.jwtauth.service.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-details")
public class BankController {

    private static final Logger logger = LoggerFactory.getLogger("API_LOG");

    private final BankService bankService;

    // Constructor injection
    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserRequestDTO> getUserDetails(@PathVariable Long userId) {
        // Log API call here
        logger.info("Calling external API to fetch user details for userId: {}", userId);
        return handleResponse(() -> bankService.getUserDetailsFromBank(userId));
    }

    @PostMapping("/save")
    public ResponseEntity<UserRequestDTO> createUserDetails(@RequestBody UserRequestDTO requestBody) {
        // Log API call here
        logger.info("Calling external API to create user details: {}", requestBody);
        return handleResponse(() -> bankService.createUserDetailsFromBank(requestBody));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserRequestDTO> updateUserDetails(@PathVariable Long userId, @RequestBody Object requestBody) {
        // Log API call here
        logger.info("Calling external API to update user details for userId: {}", userId);
        return handleResponse(() -> bankService.updateUserDetailsFromBank(userId, requestBody));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserRequestDTO> deleteUserDetails(@PathVariable Long userId) {
        // Log API call here
        logger.info("Calling external API to delete user details for userId: {}", userId);
        return handleResponse(() -> bankService.deleteUserDetailsFromBank(userId));
    }

    // Generic handler for responses
    private ResponseEntity<UserRequestDTO> handleResponse(ResponseSupplier supplier) {
        try {
            UserRequestDTO result = supplier.get();
            if (result != null) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error handling request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Functional interface to supply responses
    @FunctionalInterface
    private interface ResponseSupplier {
        UserRequestDTO get() throws Exception;
    }
}
