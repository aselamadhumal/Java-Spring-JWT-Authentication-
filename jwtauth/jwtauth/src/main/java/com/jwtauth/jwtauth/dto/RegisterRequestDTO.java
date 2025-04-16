package com.jwtauth.jwtauth.dto;

import com.jwtauth.jwtauth.annotations.email.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
    private String username;

    //@Email
    //@NotBlank(message = "Email should not be blank")
    @UniqueEmail(message = "Email must be unique")
    private String email;
    private String password;

}