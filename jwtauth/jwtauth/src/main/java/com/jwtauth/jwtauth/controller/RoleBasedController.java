package com.jwtauth.jwtauth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;


@RestController
@RequestMapping("/api/v1/auth")
public class RoleBasedController {


    @GetMapping("/user")
    @PreAuthorize("hasAuthority('APP_USER')")
    public String userPage(@RequestAttribute("user") Collection<? extends GrantedAuthority> roles) {
        return "User Access Granted. Your roles: " + roles.toString();
    }
}
