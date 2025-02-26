package com.jwtauth.jwtauth.repository;


import com.jwtauth.jwtauth.model.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, String> {
    boolean existsByToken(String token);
}