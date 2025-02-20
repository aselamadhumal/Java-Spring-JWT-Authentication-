package com.jwtauth.jwtauth.dto;

public class LoginRequestDTO {

    private String username;
    private String password;

    // Default constructor
    public LoginRequestDTO() {
    }

    // All-arguments constructor
    public LoginRequestDTO(String username, String password) {
        this.username = username;
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
}
