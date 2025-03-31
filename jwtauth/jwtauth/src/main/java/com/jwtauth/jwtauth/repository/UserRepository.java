package com.jwtauth.jwtauth.repository;

import com.jwtauth.jwtauth.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {  // ID should be Long, not String

    Optional<UserEntity> findByUsername(String username);

    boolean existsUserByEmail(String email);

    boolean existsUserEntityByPhoneNo(String phoneNo);



    @Query("SELECT u FROM UserEntity u WHERE u.nic IN :nics")
    List<UserEntity> findAllByNicIn(@Param("nics") List<String> nics);
}
