package com.jwtauth.jwtauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwtauth.jwtauth.dto.UserRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BankService {

    private static final Logger logger = LoggerFactory.getLogger(BankService.class);

    private final ApiConnector apiConnector;
    private final ObjectMapper objectMapper;

    public BankService(ApiConnector apiConnector, ObjectMapper objectMapper) {
        this.apiConnector = apiConnector;
        this.objectMapper = objectMapper;
    }

    public UserRequestDTO getUserDetailsFromBank(Long userId) {
        return callApi(() -> apiConnector.geCoreBank("users/" + userId));
    }

    public UserRequestDTO createUserDetailsFromBank(UserRequestDTO requestBody) {
//        return callApi(() -> apiConnector.createCoreBank("users/" , toJson(requestBody)));
        return callApi(() -> apiConnector.createCoreBank("users/save" , requestBody));
    }

    public UserRequestDTO updateUserDetailsFromBank(Long userId, Object requestBody) {
        return callApi(() -> apiConnector.updateCoreBank("users/" + userId, toJson(requestBody)));
    }

    public UserRequestDTO deleteUserDetailsFromBank(Long userId) {
        return callApi(() -> apiConnector.deleteCoreBank("users/" + userId));
    }

    private UserRequestDTO callApi(ApiCall apiCall) {
        try {
            String response = apiCall.execute();
            return (response == null || response.isEmpty()) ? null : objectMapper.readValue(response, UserRequestDTO.class);
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON: {}", e.getMessage());
            return null;
        }
    }

    private String toJson(Object requestBody) {
        try {
            return objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing request body: {}", e.getMessage());
            return "{}"; // Return an empty JSON object to avoid breaking the API call
        }
    }

    @FunctionalInterface
    private interface ApiCall {
        String execute() throws JsonProcessingException;
    }
}

