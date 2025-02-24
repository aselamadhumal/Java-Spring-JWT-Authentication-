package com.jwtauth.jwtauth.service;

import com.jwtauth.jwtauth.model.TokenEntity;
import com.jwtauth.jwtauth.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    /*private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }*/

    @Autowired
    private TokenRepository tokenRepository;

    public void blacklistToken(String token) {
        TokenEntity tokenEntity = new TokenEntity(token, LocalDateTime.now());
        tokenRepository.save(tokenEntity);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenRepository.existsByToken(token);
    }
}
