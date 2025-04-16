package com.jwtauth.jwtauth.utils;

public final class MessageConstantUtil {
    private MessageConstantUtil() {} // Prevent instantiation

    // User registration messages
    public static final String ALREADY_EXIST_ACCOUNT = "Username already exists";
    public static final String ALREADY_HAS_EMAIL = "Email already exists";
    public static final String INVALID_NIC = "Invalid NIC format";
    public static final String UNIQUE_NIC = "NIC number must be unique";
    public static final String USER_REGISTERED_SUCCESSFULLY = "User registered successfully";
    public static final String INVALID_PASSWORD_PATTERN = "Invalid password pattern";
    public static final String REGISTRATION_FAILED = "User registration failed";
    public static final String REFRESH_TOKEN_FAILED = "failed to generate refresh token";
    public static final String USER_NOT_FOUND = "failed to find user";
    public static final String INVALID_REFRESH_TOKEN = "Invalid or expired refresh token for user";

    public static final String UNEXPECTED_ERROR = "An unexpected error occurred";

    // Validation messages
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String VALIDATION_ERROR = "Validation error";
}