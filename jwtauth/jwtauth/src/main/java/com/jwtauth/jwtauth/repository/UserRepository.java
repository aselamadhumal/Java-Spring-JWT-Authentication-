package com.jwtauth.jwtauth.repository;

import com.jwtauth.jwtauth.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByUsername(String username);
    //UserEntity findByReferencesID(String referencesID);

    //boolean existsByReferencesID(String referencesID);

    //Optional<UserEntity> findByReferencesID(String referencesID);
}