package com.jwtauth.jwtauth.dto;

import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public class LoginResponseDTO {

    private String token;
    private LocalDateTime time;
    private String error;
    private String message;
    private String reToken;

    public LoginResponseDTO(String token) {
        this.token = token;
    }

    public String getReToken() {
        return reToken;
    }

    public void setReToken(String reToken) {
        this.reToken = reToken;
    }



    // All-arguments constructor
    public LoginResponseDTO(String token, LocalDateTime time, String error, String message, String reToken) {
        this.token = token;
        this.time = time;
        this.error = error;
        this.message = message;
        this.reToken = reToken;
    }

    // Getter and Setter for token
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // Getter and Setter for time
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    // Getter and Setter for error
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    // Getter and Setter for message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}