package com.jwtauth.jwtauth.dto;

import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public class LoginResponseDTO {

    private String access_token;
    private LocalDateTime time;
    private String error;
    private String message;
    private String reToken;


    public LoginResponseDTO(String access_token) {
        this.access_token = access_token;
    }

    public String getReToken() {
        return reToken;
    }

    public void setReToken(String reToken) {
        this.reToken = reToken;
    }



    // All-arguments constructor
    public LoginResponseDTO(String access_token, LocalDateTime time, String error, String message, String reToken) {
        this.access_token = access_token;
        this.time = time;
        this.error = error;
        this.message = message;
        this.reToken = reToken;
    }

    // Getter and Setter for token
    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
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