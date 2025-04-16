package com.jwtauth.jwtauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
public class LoginResponseDTO {

    @JsonProperty("access_token")
    private String accessToken;
    private String time;
    private String reToken;
}