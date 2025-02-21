package com.jwtauth.jwtauth.controller;


import com.jwtauth.jwtauth.service.JWTService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class HomeController {

    private final JWTService jwtService;
    public HomeController(JWTService jwtService) {
        this.jwtService = jwtService;
    }
    @GetMapping
    public String getHome() {
        return "Hello World";
    }

    /*@PostMapping("/login")
    public String login() {
        return jwtService.getJWTToken();
    }*/

    @GetMapping("/username")
    public String getUserName(@RequestParam String token){
        return jwtService.getUserName(token);
    }




}