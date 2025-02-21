package com.jwtauth.jwtauth.dto;

public class RefreshTokenResponseDTO {

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getReToken() {
        return reToken;
    }

    public void setReToken(String reToken) {
        this.reToken = reToken;
    }

    public RefreshTokenResponseDTO(String reToken, String token) {
        this.reToken = reToken;
        this.token = token;
    }

    private String token;
    private String reToken;





}
