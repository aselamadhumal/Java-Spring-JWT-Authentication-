package com.jwtauth.jwtauth.service;

import com.jwtauth.jwtauth.entity.users.UserEntity;
import com.jwtauth.jwtauth.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPaginationService {

    private final UserRepository userRepository;

    public UserPaginationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserEntity> findUserWithSorting(String field) {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, field));
    }

    public Page<UserEntity> findUsersWithPagination(int offset,int pageSize){
        Page<UserEntity> users = userRepository.findAll(PageRequest.of(offset, pageSize));
        return users;
    }

    public Page<UserEntity> findUsersWithPaginationAndSorting(int offset,int pageSize,String field){
        Page<UserEntity> users = userRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)));
        return  users;
    }


}
