package com.jwtauth.jwtauth.service;

import com.jwtauth.jwtauth.dto.TransactionRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class); // Logger instance

    private final EncDecService encDecService;

    @Autowired
    public TransactionService(EncDecService encDecService) {
        this.encDecService = encDecService;
    }

    public String performTransaction(TransactionRequestDTO request) {
        try {
            logger.info("Starting transaction for account: {}", request.getAccountNo());

            // Encrypt sensitive information (e.g., account number, NIC)
            String decryptedAccountNo = encDecService.decrypt(request.getAccountNo());
            String decryptedNic = encDecService.decrypt(request.getNic());

            // Simulate transaction logic (using encrypted data)
            if (request.getAmount() <= 0) {
                logger.error("Transaction failed: Amount must be greater than zero for account: {}", request.getAccountNo());
                return "Transaction failed: Amount must be greater than zero.";
            }

            return "Transaction successful for account: " + decryptedAccountNo +
                    " | Amount: " + request.getAmount() +
                    " | NIC: " + decryptedNic;

        } catch (Exception e) {
            logger.error("Error during transaction for account: {}: {}", request.getAccountNo(), e.getMessage(), e);
            return "Error during transaction: " + e.getMessage();
        }
    }
}
