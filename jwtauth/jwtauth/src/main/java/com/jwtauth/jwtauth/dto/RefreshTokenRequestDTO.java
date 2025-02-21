package com.jwtauth.jwtauth.dto;

public class RefreshTokenRequestDTO {
    private String reToken;
    private String username;

    // Default constructor required for deserialization
    public RefreshTokenRequestDTO() {
    }

    // Constructor to initialize both fields
    public RefreshTokenRequestDTO(String reToken, String username) {
        this.reToken = reToken;
        this.username = username;
    }

    public String getReToken() {
        return reToken;
    }

    public void setReToken(String reToken) {
        this.reToken = reToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
