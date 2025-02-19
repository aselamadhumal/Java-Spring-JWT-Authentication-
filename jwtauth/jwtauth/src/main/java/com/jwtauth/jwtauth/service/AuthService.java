package com.jwtauth.jwtauth.service;


import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder  passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    //when reqvest details
    public UserEntity createUser(UserEntity userData) {
        userData.setPassword(passwordEncoder.encode(userData.getPassword()));

        return userRepository.save(userData);
    }

    public UserEntity login(UserEntity userData) {
        userData.setPassword(passwordEncoder.encode(userData.getPassword()));

        return userRepository.save(userData);
    }




}
