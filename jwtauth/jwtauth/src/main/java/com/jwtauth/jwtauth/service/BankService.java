package com.jwtauth.jwtauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwtauth.jwtauth.dto.UserRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BankService {

    private static final Logger logger = LoggerFactory.getLogger("API_LOG");

    private final ApiConnector apiConnector;
    private final ObjectMapper objectMapper;

    public BankService(ApiConnector apiConnector, ObjectMapper objectMapper) {
        this.apiConnector = apiConnector;
        this.objectMapper = objectMapper;
    }

    public UserRequestDTO getUserDetailsFromBank(Long userId) {
        logger.info("Fetching user details from bank for userId: {}", userId);
        return callApi(() -> apiConnector.geCoreBank("users/" + userId));
    }

    public UserRequestDTO createUserDetailsFromBank(UserRequestDTO requestBody) {
        logger.info("Creating user details in bank with request body: {}", toJson(requestBody));
        return callApi(() -> apiConnector.createCoreBank("users/save", requestBody));
    }

    public UserRequestDTO updateUserDetailsFromBank(Long userId, Object requestBody) {
        logger.info("Updating user details in bank for userId: {} with request body: {}", userId, toJson(requestBody));
        return callApi(() -> apiConnector.updateCoreBank("users/" + userId, toJson(requestBody)));
    }

    public UserRequestDTO deleteUserDetailsFromBank(Long userId) {
        logger.info("Deleting user details from bank for userId: {}", userId);
        return callApi(() -> apiConnector.deleteCoreBank("users/" + userId));
    }

    private UserRequestDTO callApi(ApiCall apiCall) {
        try {
            logger.debug("Calling external API...");
            String response = apiCall.execute();
            logger.debug("API response: {}", response);
            return (response == null || response.isEmpty()) ? null : objectMapper.readValue(response, UserRequestDTO.class);
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON: {}", e.getMessage(), e);
            return null;
        }
    }

    private String toJson(Object requestBody) {
        try {
            return objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing request body: {}", e.getMessage(), e);
            return "{}"; // Return an empty JSON object to avoid breaking the API call
        }
    }

    @FunctionalInterface
    private interface ApiCall {
        String execute() throws JsonProcessingException;
    }
}
