package com.jwtauth.jwtauth.exceptions;

public class InvalidJwtTokenException extends RuntimeException{

    public InvalidJwtTokenException() {super("Invalid JWT token");}
}
