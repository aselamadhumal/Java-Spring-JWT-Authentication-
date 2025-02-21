package com.jwtauth.jwtauth.dto;

public class RegisterRequestDTO {
    private String username;
    private String email;
    private String password;



    // Default constructor
    public RegisterRequestDTO() {
    }

    // Parameterized constructor
    public RegisterRequestDTO(String username, String email,String password) {
        this.username = username;
        this.email = email;
        this.password = password;

    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email
    public void setEmail(String email) {
        this.email = email;
    }


}