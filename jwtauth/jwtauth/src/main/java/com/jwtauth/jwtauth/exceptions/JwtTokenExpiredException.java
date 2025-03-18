package com.jwtauth.jwtauth.exceptions;

public class JwtTokenExpiredException extends RuntimeException {
    public JwtTokenExpiredException() {
        super("JWT Token has expired");
    }

}
