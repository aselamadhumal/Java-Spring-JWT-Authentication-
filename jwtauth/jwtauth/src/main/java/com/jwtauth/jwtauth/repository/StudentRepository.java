package com.jwtauth.jwtauth.repository;

import com.jwtauth.jwtauth.model.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
}
