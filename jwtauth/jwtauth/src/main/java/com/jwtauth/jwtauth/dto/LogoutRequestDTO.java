package com.jwtauth.jwtauth.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LogoutRequestDTO {

    private String accessToken;

}
