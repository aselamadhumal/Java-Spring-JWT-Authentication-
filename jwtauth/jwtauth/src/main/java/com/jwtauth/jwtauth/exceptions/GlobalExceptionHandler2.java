package com.jwtauth.jwtauth.exceptions;


import com.jwtauth.jwtauth.dto.BaseResponse;
import com.jwtauth.jwtauth.utils.MessageConstantUtil;
import com.jwtauth.jwtauth.utils.ResponseUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler2{

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return BaseResponse.<Map<String, String>>builder()
                .code(ResponseUtil.FAILED_CODE)
                .title(ResponseUtil.FAILED)
                .message(MessageConstantUtil.VALIDATION_FAILED)
                .data(errors)
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return BaseResponse.<Map<String, String>>builder()
                .code(ResponseUtil.FAILED_CODE)
                .title(ResponseUtil.FAILED)
                .message("Validation error")
                .data(errors)
                .build();
    }
}
