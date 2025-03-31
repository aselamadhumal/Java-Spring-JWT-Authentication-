package com.jwtauth.jwtauth.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Product2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
}

