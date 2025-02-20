package com.jwtauth.jwtauth.model;

import jakarta.persistence.*;
import lombok.Builder;

import java.time.Instant;

@Builder
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long path_id;

    @Column(nullable = false, unique = true)
    private String reToken;

    @Column(nullable = false)
    private Instant expiryDate;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private UserEntity userEntity;

    // Default constructor
    public RefreshToken() {
    }

    // Parameterized constructor
    public RefreshToken(long path_id, String reToken, Instant expiryDate, UserEntity userEntity) {
        this.path_id = path_id;
        this.reToken = reToken;
        this.expiryDate = expiryDate;
        this.userEntity = userEntity;
    }

    // Getters and Setters
    public long getPath_id() {
        return path_id;
    }

    public void setPath_id(long path_id) {
        this.path_id = path_id;
    }

    public String getReToken() {
        return reToken;
    }

    public void setReToken(String reToken) {
        this.reToken = reToken;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}
