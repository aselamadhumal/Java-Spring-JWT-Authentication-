package com.jwtauth.jwtauth.service;

import com.jwtauth.jwtauth.dto.TransactionRequestDTO;
import com.jwtauth.jwtauth.entity.TransactionEntity;
import com.jwtauth.jwtauth.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class); // Logger instance
    private final EncDecService encDecService;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(EncDecService encDecService, TransactionRepository transactionRepository) {
        this.encDecService = encDecService;
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionEntity> getAllTransactions() {
        logger.info("Fetching all Transactions");
        return transactionRepository.findAll();
    }

    public String performTransaction(TransactionRequestDTO request) {
        try {
            logger.info("Starting transaction for account: {}", request.getAccountNo());

            // Decrypt sensitive information
            String decryptedAccountNo = encDecService.decrypt(request.getAccountNo());
            String decryptedNic = encDecService.decrypt(request.getNic());

            // Validate transaction amount
            if (request.getAmount() <= 0) {
                logger.error("Transaction failed: Amount must be greater than zero for account: {}", request.getAccountNo());
                return "Transaction failed: Amount must be greater than zero.";
            }

            // Create and save the transaction
            TransactionEntity transaction = new TransactionEntity();
            transaction.setAccountNo(decryptedAccountNo);
            transaction.setNic(decryptedNic);
            transaction.setAmount(request.getAmount());
            transaction.setRemark(request.getRemark());

            TransactionEntity savedTransaction = transactionRepository.save(transaction);
            logger.info("Transaction saved with ID: {}", savedTransaction.getId());

            return "Transaction successful for account: " + decryptedAccountNo +
                    " | Amount: " + request.getAmount() +
                    " | NIC: " + decryptedNic +
                    " | Remark:" + request.getRemark();

        } catch (Exception e) {
            logger.error("Error during transaction for account: {}: {}", request.getAccountNo(), e.getMessage(), e);
            return "Error during transaction: " + e.getMessage();
        }
    }
}
