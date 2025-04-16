package com.jwtauth.jwtauth.service;

import com.jwtauth.jwtauth.dto.*;
import com.jwtauth.jwtauth.entity.users.Role;
import com.jwtauth.jwtauth.exceptions.AuthenticationException;
import com.jwtauth.jwtauth.exceptions.LoginFailedException;
import com.jwtauth.jwtauth.exceptions.UserNotFoundException;
import com.jwtauth.jwtauth.entity.users.UserEntity;
import com.jwtauth.jwtauth.repository.UserRepository;
import com.jwtauth.jwtauth.utils.Constants;
import com.jwtauth.jwtauth.utils.MessageConstantUtil;
import com.jwtauth.jwtauth.utils.ResponseUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static com.jwtauth.jwtauth.utils.PhoneNumberUtil.formatNumber;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RoleService roleService;

    public List<UserEntity> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll();
    }

    public void persistUser(UserEntity user) {
        try {
            userRepository.save(user);
        } catch (Exception e) {
            logger.error("persistUser-> Exception: {}", e.getMessage(), e);
        }
    }

    private UserEntity createUser(RegisterRequestDTO userData) {
        logger.info("Creating user with username: {}", userData.getUsername());

        UserEntity newUser = new UserEntity();
        newUser.setUsername(userData.getUsername());
        newUser.setPassword(passwordEncoder.encode(userData.getPassword()));
        newUser.setEmail(userData.getEmail());
        newUser.setPhoneNo(formatNumber(userData.getPhoneNo()));
        newUser.setNic(userData.getNic());

        Role role = roleService.getRoleByName(Constants.ROLE_APP_USER);
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(role);
        newUser.setRoles(userRoles);
        persistUser(newUser);

        // Log the user registration details
        logger.info("User saved with username: {}, email: {}, phone: {}, nic: {}",
                userData.getUsername(), userData.getEmail(), userData.getPhoneNo(), userData.getNic());

        // Save the new user
        UserEntity savedUser = userRepository.save(newUser);
        logger.info("User created with ID: {}", savedUser.getId());
        return savedUser;

    }

    public BaseResponse<HashMap<String, Object>> registerUser(@Valid RegisterRequestDTO registerData) {
        logger.info("Registering user with username: {}", registerData.getUsername());

        try {

            UserEntity registrationSuccessful = this.createUser(registerData);
            if (registrationSuccessful != null) {
                return BaseResponse.<HashMap<String, Object>>builder()
                        .code(ResponseUtil.SUCCESS_CODE)
                        .title(ResponseUtil.SUCCESS)
                        .message(MessageConstantUtil.USER_REGISTERED_SUCCESSFULLY)
                        .build();
            } else {
                return BaseResponse.<HashMap<String, Object>>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message(MessageConstantUtil.REGISTRATION_FAILED)
                        .build();
            }

        } catch (Exception e) {
            logger.error("Error during user registration", e);
            return BaseResponse.<HashMap<String, Object>>builder()
                    .code(ResponseUtil.FAILED_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("An unexpected error occurred during registration")
                    .build();
        }
    }

    public BaseResponse<LoginResponseDTO> login(LoginRequestDTO loginData) {
        String referencesID = UUID.randomUUID().toString();
        logger.info("Reference ID: {}", referencesID);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword())
            );
        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed due to invalid credentials.");
            throw new LoginFailedException("Invalid username or password.");
        } catch (Exception e) {
            logger.error("Unexpected authentication error: {}", e.getMessage(), e);
            throw new AuthenticationException("An unexpected error occurred during authentication.");
        }

        // Validate user existence before token generation
        UserEntity user = userRepository.findByUsername(loginData.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + loginData.getUsername()));

        // Token generation logic
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.USERNAME, loginData.getUsername());
        claims.put(Constants.REFERENCE, referencesID);

        String accessToken = jwtService.getJWTToken(loginData.getUsername(), claims);
        String refreshToken = jwtService.getRefreshToken(loginData.getUsername(), claims);

        // Save user details
        user.setReferencesID(referencesID);
        user.setExpireAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        logger.info("Token generated successfully for user.");

        return BaseResponse.<LoginResponseDTO>builder()
                .code(ResponseUtil.SUCCESS_CODE)
                .message("Login successful")
                .data(LoginResponseDTO.builder()
                        .time(LocalDateTime.now().toString())
                        .accessToken(accessToken)
                        .reToken(refreshToken)
                        .build())
                .build();
    }

    public BaseResponse<Map<String, Object>> logout(String accessToken) {
        try {
            // Validate token and extract claims
            Claims claims = jwtService.getTokenData(accessToken);
            if (claims == null) {
                logger.warn("Invalid token claims");
                return BaseResponse.<Map<String, Object>>builder()
                        .code(ResponseUtil.UNAUTHORIZED_CODE)
                        .title(ResponseUtil.UNAUTHORIZED)
                        .message("Invalid token")
                        .data(null)
                        .build();
            }

            String reference = claims.get(Constants.REFERENCE, String.class);
            String username = claims.getSubject();

            if (!StringUtils.hasLength(reference)) {
                logger.warn("No reference ID found in token for user: {}", username);
                return BaseResponse.<Map<String, Object>>builder()
                        .code(ResponseUtil.BAD_REQUEST_CODE)
                        .title(ResponseUtil.BAD_REQUEST)
                        .message("Reference ID not found in token")
                        .data(null)
                        .build();
            }

            // Find and update user
            Optional<UserEntity> userEntity = userRepository.findByUsername(username);
            if (userEntity.isEmpty()) {
                logger.warn("No user found with username: {}", username);
                return BaseResponse.<Map<String, Object>>builder()
                        .code(ResponseUtil.NOT_FOUND_CODE)
                        .title(ResponseUtil.NOT_FOUND)
                        .message("User not found")
                        .data(null)
                        .build();
            }

            UserEntity user = userEntity.get();
            user.setReferencesID(null);
            userRepository.save(user);

            logger.info("User {} logged out successfully, reference ID cleared", username);

            // Return success response with relevant data
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("username", username);
            responseData.put("reference", reference);
            responseData.put("logoutTime", Instant.now().toString());

            return BaseResponse.<Map<String, Object>>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Logout successful")
                    .data(responseData)
                    .build();

        } catch (Exception e) {
            logger.error("Logout failed: {}", e.getMessage(), e);
            return BaseResponse.<Map<String, Object>>builder()
                    .code(ResponseUtil.INTERNAL_SERVER_ERROR_CODE)
                    .title(ResponseUtil.INTERNAL_SERVER_ERROR)
                    .message("Logout processing failed")
                    .data(null)
                    .build();
        }
    }

    public BaseResponse<RefreshTokenResponseDTO> refresh(RefreshTokenRequestDTO refreshRequest) {
        try {
            String refreshRequestId = UUID.randomUUID().toString();
            logger.info("Received refresh token request with ID: {}", refreshRequestId);

            // Extract and validate the refresh token
            String refreshToken = refreshRequest.getRefreshToken();
            if (refreshToken == null || refreshToken.isEmpty()) {
                logger.warn("Empty refresh token provided");
                return BaseResponse.<RefreshTokenResponseDTO>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message(MessageConstantUtil.REFRESH_TOKEN_FAILED)
                        .data(null)
                        .build();
            }

            // Validate refresh token
            String username = jwtService.extractUsername(refreshToken);
            if (username == null || !jwtService.isTokenValid(refreshToken, username)) {
                logger.warn("Invalid or expired refresh token for user: {}", username);
                return BaseResponse.<RefreshTokenResponseDTO>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message(MessageConstantUtil.INVALID_REFRESH_TOKEN)
                        .data(null)
                        .build();
            }

            logger.debug("Validating refresh token: {}", refreshToken);
            logger.info("Extracted username from refresh token: {}", username);

            // Retrieve user from the repository
            Optional<UserEntity> userEntity = userRepository.findByUsername(username);
            if (userEntity.isEmpty()) {
                logger.warn("No user found with username: {}", username);
                return BaseResponse.<RefreshTokenResponseDTO>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message(MessageConstantUtil.USER_NOT_FOUND)
                        .data(null)
                        .build();
            }


            UserEntity user = userEntity.get();
            user.setReferencesID(refreshRequestId);
            userRepository.save(user);

            Map<String, Object> claims = new HashMap<>();
            claims.put("username", username);
            claims.put("reference", refreshRequestId);

            String newAccessToken = jwtService.getJWTToken(username, claims);
            String newRefreshToken = jwtService.getRefreshToken(username, claims);

            logger.info("Tokens refreshed successfully for user: {}", username);


            RefreshTokenResponseDTO responseDTO = RefreshTokenResponseDTO.builder()
                    .token(newAccessToken)
                    .reToken(newRefreshToken)
                    .build();

            return BaseResponse.<RefreshTokenResponseDTO>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .message("Refresh Token Generated Successfully")
                    .data(responseDTO)
                    .build();

        } catch (ExpiredJwtException e) {
            logger.error("Refresh token expired: {}", e.getMessage());
            return BaseResponse.<RefreshTokenResponseDTO>builder()
                    .code(ResponseUtil.FAILED_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Refresh token expired")
                    .data(null)
                    .build();
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage(), e);
            return BaseResponse.<RefreshTokenResponseDTO>builder()
                    .code(ResponseUtil.FAILED_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Token refresh failed due to an internal error")
                    .data(null)
                    .build();
        }
    }
}













    

