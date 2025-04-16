package com.jwtauth.jwtauth.repository;

import com.jwtauth.jwtauth.entity.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findRoleById(long id);
    Role findRoleByName(String name);
}
