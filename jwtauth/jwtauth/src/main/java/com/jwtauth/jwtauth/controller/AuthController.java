package com.jwtauth.jwtauth.controller;

import com.jwtauth.jwtauth.dto.*;
import com.jwtauth.jwtauth.entity.users.UserEntity;
import com.jwtauth.jwtauth.service.AuthService;
import com.jwtauth.jwtauth.service.UserPaginationService;
import com.jwtauth.jwtauth.utils.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final UserPaginationService userPaginationService;
    private final ResponseHandler responseHandler;


    @GetMapping("/list")
    public List<UserEntity> getAllUsers() {
        return authService.getAllUsers();
    }

    @PostMapping("/login")
    public ResponseEntity<DefaultResponse> login(@Valid @RequestBody LoginRequestDTO loginData) {
        BaseResponse<LoginResponseDTO> response = authService.login(loginData);
        return responseHandler.handleResponse(response);
    }

    @PostMapping("/register")
    public ResponseEntity<DefaultResponse>registerUser(@Valid @RequestBody RegisterRequestDTO registerData){
        BaseResponse<HashMap<String, Object>> response = authService.registerUser(registerData);
        return responseHandler.handleResponse(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<DefaultResponse>refresh(@Valid @RequestBody RefreshTokenRequestDTO registerData){
        BaseResponse<RefreshTokenResponseDTO> response = authService.refresh(registerData);
        return responseHandler.handleResponse(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<DefaultResponse> logout(@Valid @RequestBody LogoutRequestDTO logoutData) {
        BaseResponse<Map<String, Object>> response = authService.logout(logoutData.getAccessToken());
        return responseHandler.handleResponse(response);
    }

    @GetMapping("/{field}")
    public APIResponseDTO<List<UserEntity>> getUsersWithSort(@PathVariable String field) {
        List<UserEntity> allUsers = userPaginationService.findUserWithSorting(field);
        return new APIResponseDTO<>(allUsers.size(), allUsers);
    }

    @GetMapping("/pagination")
    public APIResponseDTO<Page<UserEntity>> getUsersWithPagination(
            @RequestParam String offset,
            @RequestParam String pageSize) {
        int of = Integer.parseInt(offset);
        int page = Integer.parseInt(pageSize);
        logger.info("Pagination request received - Offset: {}, PageSize: {}", of, page);
        Page<UserEntity> usersWithPagination = userPaginationService.findUsersWithPagination(of, page);
        logger.info("Pagination response - Total elements in current page: {}, Total elements overall: {}",
                usersWithPagination.getNumberOfElements(),
                usersWithPagination.getTotalElements());
        return new APIResponseDTO<>(usersWithPagination.getNumberOfElements(), usersWithPagination);
    }

    @GetMapping("/paginationAndSort/{offset}/{pageSize}/{field}")
    public APIResponseDTO<Page<UserEntity>> getUsersWithPaginationAndSort(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field) {
        Page<UserEntity> usersWithPagination = userPaginationService.findUsersWithPaginationAndSorting(offset, pageSize, field);
        return new APIResponseDTO<>(usersWithPagination.getNumberOfElements(), usersWithPagination);
    }
}