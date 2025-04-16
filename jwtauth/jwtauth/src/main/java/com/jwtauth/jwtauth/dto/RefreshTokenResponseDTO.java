package com.jwtauth.jwtauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
public class RefreshTokenResponseDTO {

    @JsonProperty("access_token")
    private String token;
    @JsonProperty("refresh_token")
    private String reToken;

}
