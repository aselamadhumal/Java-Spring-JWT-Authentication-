package com.jwtauth.jwtauth.controller;

import com.jwtauth.jwtauth.dto.TransactionRequestDTO;
import com.jwtauth.jwtauth.service.EncDecService;
import com.jwtauth.jwtauth.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class); // Logger instance

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private EncDecService encDecService;

    // Endpoint to encrypt data
    @PostMapping("/enc")
    public ResponseEntity<String> encrypt(@RequestBody String data) {
        try {
            String encryptedData = encDecService.encrypt(data);
            return ResponseEntity.ok(encryptedData);
        } catch (NoSuchPaddingException e) {
            logger.error("Encryption failed due to padding issue: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Encryption failed due to padding issue: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            logger.error("Encryption failed due to block size issue: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Encryption failed due to block size issue: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Encryption failed due to algorithm issue: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Encryption failed due to algorithm issue: " + e.getMessage());
        } catch (BadPaddingException e) {
            logger.error("Encryption failed due to bad padding: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Encryption failed due to bad padding: " + e.getMessage());
        } catch (InvalidKeyException e) {
            logger.error("Encryption failed due to invalid key: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Encryption failed due to invalid key: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during encryption: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Unexpected error during encryption: " + e.getMessage());
        }
    }

    // Endpoint to decrypt data
    @PostMapping("/dec")
    public ResponseEntity<String> decrypt(@RequestBody String encryptedData) {
        try {
            String decryptedData = encDecService.decrypt(encryptedData);
            return ResponseEntity.ok(decryptedData);
        } catch (NoSuchPaddingException e) {
            logger.error("Decryption failed due to padding issue: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Decryption failed due to padding issue: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            logger.error("Decryption failed due to block size issue: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Decryption failed due to block size issue: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Decryption failed due to algorithm issue: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Decryption failed due to algorithm issue: " + e.getMessage());
        } catch (BadPaddingException e) {
            logger.error("Decryption failed due to bad padding: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Decryption failed due to bad padding: " + e.getMessage());
        } catch (InvalidKeyException e) {
            logger.error("Decryption failed due to invalid key: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Decryption failed due to invalid key: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during decryption: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Unexpected error during decryption: " + e.getMessage());
        }
    }

    // Endpoint to process a transaction
    @PostMapping("/process")
    public ResponseEntity<String> processTransaction(@RequestBody TransactionRequestDTO requestDTO) {
        try {
            String response = transactionService.performTransaction(requestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Transaction processing failed for account {}: {}", requestDTO.getAccountNo(), e.getMessage(), e);
            return ResponseEntity.status(500).body("Transaction processing failed: " + e.getMessage());
        }
    }
}