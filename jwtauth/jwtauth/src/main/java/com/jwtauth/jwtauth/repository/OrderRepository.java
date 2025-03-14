package com.jwtauth.jwtauth.repository;
import com.jwtauth.jwtauth.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity,Integer> {
}
