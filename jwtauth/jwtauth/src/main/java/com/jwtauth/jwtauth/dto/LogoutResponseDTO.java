package com.jwtauth.jwtauth.dto;

public class LogoutResponseDTO {

    public LogoutResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public String getReferencesID() {
        return referencesID;
    }

    public void setReferencesID(String referencesID) {
        this.referencesID = referencesID;
    }

    private String referencesID;

}
