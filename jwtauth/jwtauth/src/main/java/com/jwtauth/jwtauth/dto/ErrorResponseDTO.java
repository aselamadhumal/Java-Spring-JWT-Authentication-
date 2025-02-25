package com.jwtauth.jwtauth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseDTO {
    private String code;
    private String message;

    public ErrorResponseDTO(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
