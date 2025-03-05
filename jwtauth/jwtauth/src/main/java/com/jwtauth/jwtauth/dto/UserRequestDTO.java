package com.jwtauth.jwtauth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    private String name;
    private int age;
    private String email;
    private String password;

    // Default constructor
    public UserRequestDTO() {}

    // Parameterized constructor
    public UserRequestDTO(String name, int age, String email, String password) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
    }


}
