package com.jwtauth.jwtauth.dto;

import com.jwtauth.jwtauth.annotations.email.UniqueEmail;
import com.jwtauth.jwtauth.annotations.mobile.UniqueMobile;
import com.jwtauth.jwtauth.annotations.mobile.ValidMobile;
import com.jwtauth.jwtauth.annotations.name.UniqueName;
import com.jwtauth.jwtauth.annotations.nic.UniqueNic;
import com.jwtauth.jwtauth.annotations.nic.ValidNic;
import com.jwtauth.jwtauth.annotations.username.ValidPassword;
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

    @UniqueName
    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email should not be blank")
    @UniqueEmail(message = "Email must be unique")
    private String email;

    @ValidPassword
    @NotBlank(message = "Password is required")
    private String password;

    @ValidMobile
    @UniqueMobile
    @NotBlank(message = "Phone number is required")
    private String phoneNo;

    @ValidNic
    @UniqueNic
    @NotBlank(message = "NIC is required")
    private String nic;
}
