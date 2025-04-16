package com.jwtauth.jwtauth.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginFailedException extends RuntimeException {
    private  String password;

    public LoginFailedException(String message) {
        super(message);

    }





}