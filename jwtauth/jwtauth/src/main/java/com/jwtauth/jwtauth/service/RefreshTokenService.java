package com.jwtauth.jwtauth.service;

import com.jwtauth.jwtauth.model.RefreshToken;
import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.repository.RefreshTokenRepository;
import com.jwtauth.jwtauth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public RefreshToken createRefreshToken(String username) {
        logger.info("Creating a new refresh token for user: {}", username);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        RefreshToken refreshToken = RefreshToken.builder()
                .userEntity(user)
                .reToken(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000)) // 10 minutes
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByReToken(String reToken) {
        return refreshTokenRepository.findByReToken(reToken);
    }

    public RefreshToken verifyRefreshToken(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            logger.warn("Refresh token expired for user: {}", refreshToken.getUserEntity().getUsername());
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired. Please log in again.");
        }

        return refreshToken;
    }

    public void deleteRefreshToken(RefreshToken refreshToken) {
        logger.info("Deleting old refresh token for user: {}", refreshToken.getUserEntity().getUsername());
        refreshTokenRepository.delete(refreshToken);
    }
}
