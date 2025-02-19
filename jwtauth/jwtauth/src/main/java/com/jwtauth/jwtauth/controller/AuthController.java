package com.jwtauth.jwtauth.controller;

import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.service.AuthService;
import com.jwtauth.jwtauth.service.JWTService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    private final JWTService jwtService;


    public AuthController(AuthService authService, JWTService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    // Get all users
    @GetMapping("/list")
    public List<UserEntity> getAllUsers() {
        return authService.getAllUsers();
    }

    // Create a new user
    @PostMapping("/register")
    public UserEntity createUser(@RequestBody UserEntity user) {
        return authService.createUser(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserEntity user) {
        return jwtService.getJWTToken(user.getUsername());
    }





}
