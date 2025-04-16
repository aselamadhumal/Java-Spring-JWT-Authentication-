package com.jwtauth.jwtauth.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;
}

