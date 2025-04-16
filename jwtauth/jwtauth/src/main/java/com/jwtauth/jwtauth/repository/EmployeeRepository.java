package com.jwtauth.jwtauth.repository;
import com.jwtauth.jwtauth.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;



public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {


}

