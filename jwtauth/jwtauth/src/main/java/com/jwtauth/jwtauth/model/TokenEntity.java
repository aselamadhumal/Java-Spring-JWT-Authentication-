package com.jwtauth.jwtauth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "blacklist_tokens")
public class TokenEntity {
    @Id
    @Column(name = "token", length = 700)
    private String token;
    private LocalDateTime blacklistedAt;

    // Constructors, getters, and setters
    public TokenEntity() {}

    public TokenEntity(String token, LocalDateTime blacklistedAt) {
        this.token = token;
        this.blacklistedAt = blacklistedAt;
    }


}