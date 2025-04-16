package com.jwtauth.jwtauth.dto;

public class NotificationDTO {

    private String message;


    public NotificationDTO(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


}
