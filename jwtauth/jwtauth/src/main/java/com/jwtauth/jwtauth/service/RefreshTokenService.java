package com.jwtauth.jwtauth.service;

import com.jwtauth.jwtauth.model.RefreshToken;
import com.jwtauth.jwtauth.repository.RefreshTokenRepository;
import com.jwtauth.jwtauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;

    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = RefreshToken.builder()
                .userEntity(userRepository.findByUsername(username).get())
                .reToken(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000)) // 10 minutes
                .build();
        return refreshTokenRepository.save(refreshToken); // Added return statement
    }

    public Optional<RefreshToken> findByReToken(String reToken) {
        return refreshTokenRepository.findByReToken(reToken);
    }

    public RefreshToken verifyRefreshToken(RefreshToken reToken) {
        if(reToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(reToken);
            throw new RuntimeException(reToken.getReToken()+" Refresh token was expired. Please make a new signin request");
        }

        return reToken;
    }


}