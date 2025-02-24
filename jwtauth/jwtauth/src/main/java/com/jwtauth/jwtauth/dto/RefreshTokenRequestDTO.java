package com.jwtauth.jwtauth.dto;

// Added DTO for refresh token request
public class RefreshTokenRequestDTO {
    private String refreshToken;

    // Getters and setters
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}