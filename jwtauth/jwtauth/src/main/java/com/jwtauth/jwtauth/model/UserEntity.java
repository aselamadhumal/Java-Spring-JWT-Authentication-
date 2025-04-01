package com.jwtauth.jwtauth.model;

import com.jwtauth.jwtauth.annotations.mobile.ValidMobile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "username")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;


    private String phoneNo;

    private String nic;

    private String referencesID;

    private LocalDateTime expireAt;

    @Column(nullable = false, unique = true)
    private String uuid;

    @PrePersist
    public void generateUUID() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();  // Generate UUID if not already set
        }
    }
}
