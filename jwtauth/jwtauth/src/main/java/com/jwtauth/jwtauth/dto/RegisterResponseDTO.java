package com.jwtauth.jwtauth.dto;

public class RegisterResponseDTO {


    private String error;

    public RegisterResponseDTO(String message,String error) {
        this.message = message;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public void setError(String error) {
        this.error = error;
    }

    private String message;

    public String getError() {
        return error;
    }



}
