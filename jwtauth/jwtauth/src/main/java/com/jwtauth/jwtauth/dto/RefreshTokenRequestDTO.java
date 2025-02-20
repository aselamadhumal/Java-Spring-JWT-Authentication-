package com.jwtauth.jwtauth.dto;


public class RefreshTokenRequestDTO {
    public RefreshTokenRequestDTO(String reToken) {
        this.reToken = reToken;
    }

    public String getReToken() {
        return reToken;
    }

    public void setReToken(String reToken) {
        this.reToken = reToken;
    }

    private String reToken;

}
