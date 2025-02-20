package com.jwtauth.jwtauth.repository;

import com.jwtauth.jwtauth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByReToken(String reToken);

}
