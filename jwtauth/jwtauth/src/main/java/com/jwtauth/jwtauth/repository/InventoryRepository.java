package com.jwtauth.jwtauth.repository;
import com.jwtauth.jwtauth.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<ProductEntity,Integer> {
}
