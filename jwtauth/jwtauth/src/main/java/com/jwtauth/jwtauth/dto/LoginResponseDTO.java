package com.jwtauth.jwtauth.dto;

import java.time.LocalDateTime;

public class LoginResponseDTO {

    private String token;
    private LocalDateTime time;
    private String error;
    private String message;

    // All-arguments constructor
    public LoginResponseDTO(String token, LocalDateTime time, String error, String message) {
        this.token = token;
        this.time = time;
        this.error = error;
        this.message = message;
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
