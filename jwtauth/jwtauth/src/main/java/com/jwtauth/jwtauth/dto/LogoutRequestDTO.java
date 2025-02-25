package com.jwtauth.jwtauth.dto;

import lombok.*;

@Setter
@Getter
public class LogoutRequestDTO {

    private String accessToken;
    private String refreshToken;
}
