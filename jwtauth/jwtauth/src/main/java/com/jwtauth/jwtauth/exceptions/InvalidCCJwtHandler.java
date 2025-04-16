package com.jwtauth.jwtauth.exceptions;

import com.jwtauth.jwtauth.dto.DefaultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class InvalidCCJwtHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<Object> handleInvalidJwtTokenException(InvalidJwtTokenException ex) {
        return ResponseEntity.badRequest().body(DefaultResponse.error("JWT_TOKEN_ERROR", "invalid jwt token"));
    }

    @ExceptionHandler(JwtTokenExpiredException.class)
    public ResponseEntity<Object> handleJwtTokenExpiredException(JwtTokenExpiredException ex) {
        return ResponseEntity.badRequest().body(DefaultResponse.error("JWT_TOKEN_EXPIRED", "jwt token has expired"));
    }



}
