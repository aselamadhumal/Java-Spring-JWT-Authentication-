package com.jwtauth.jwtauth.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDTO {

    private String access_token;
    private LocalDateTime time;
    private String error;
    private String message;
    private String reToken;
}