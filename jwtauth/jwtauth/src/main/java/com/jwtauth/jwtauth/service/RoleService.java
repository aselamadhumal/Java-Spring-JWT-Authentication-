package com.jwtauth.jwtauth.service;

import com.jwtauth.jwtauth.entity.users.Role;
import com.jwtauth.jwtauth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRoleByName(String name) {
        return roleRepository.findRoleByName(name);
    }

}
