package com.jwtauth.jwtauth.service;

import com.jwtauth.jwtauth.dto.*;
import com.jwtauth.jwtauth.exceptions.AuthenticationException;
import com.jwtauth.jwtauth.exceptions.LoginFailedException;
import com.jwtauth.jwtauth.exceptions.UserNotFoundException;
import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.repository.UserRepository;
import com.jwtauth.jwtauth.utils.MessageConstantUtil;
import com.jwtauth.jwtauth.utils.NicUtil;
import com.jwtauth.jwtauth.utils.ResponseUtil;
import com.jwtauth.jwtauth.validators.password.PasswordValidator;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

import static com.jwtauth.jwtauth.utils.PhoneNumberUtil.formatNumber;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final NicUtil nicUtil;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService, NicUtil nicUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.nicUtil = nicUtil;
    }

    public List<UserEntity> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll();
    }

    private UserEntity createUser(RegisterRequestDTO userData) {
        logger.info("Creating user with username: {}", userData.getUsername());

        UserEntity newUser = new UserEntity();
        newUser.setUsername(userData.getUsername());
        newUser.setPassword(passwordEncoder.encode(userData.getPassword()));
        newUser.setEmail(userData.getEmail());
        newUser.setPhoneNo(formatNumber(userData.getPhoneNo()));
        newUser.setNic(userData.getNic());

        UserEntity savedUser = userRepository.save(newUser);
        logger.info("User created with ID: {}", savedUser.getId());
        return savedUser;
    }

    public BaseResponse<Map<String, Object>> registerUser(@Valid RegisterRequestDTO registerData) {
        logger.info("Registering user with username: {}", registerData.getUsername());

        try {
            // Validation checks
            if (isUserEnabled(registerData.getUsername())) {
                return BaseResponse.failure(MessageConstantUtil.ALREADY_EXIST_ACCOUNT);
            }

            if (isEmailEnabled(registerData.getEmail())) {
                return BaseResponse.failure(MessageConstantUtil.ALREADY_HAS_EMAIL);
            }

            if (!nicUtil.isValidNic(registerData.getNic())) {
                return BaseResponse.failure(MessageConstantUtil.INVALID_NIC);
            }

            if (!nicUtil.isUniqueNic(registerData.getNic())) {
                return BaseResponse.failure(MessageConstantUtil.UNIQUE_NIC);
            }

            PasswordValidator passwordValidator = new PasswordValidator();
            if (!passwordValidator.isValid(registerData.getPassword(), null)) {
                return BaseResponse.failure(MessageConstantUtil.INVALID_PASSWORD_PATTERN);
            }

            UserEntity savedUser = createUser(registerData);

            return (savedUser != null)
                    ? BaseResponse.success(
                    Map.of("userId", savedUser.getId()),
                    MessageConstantUtil.USER_REGISTERED_SUCCESSFULLY)
                    : BaseResponse.failure(MessageConstantUtil.REGISTRATION_FAILED);

        } catch (Exception e) {
            logger.error("Error during user registration for username: {}", registerData.getUsername(), e);
            return BaseResponse.failure(MessageConstantUtil.UNEXPECTED_ERROR);
        }
    }
    /*public UserEntity createUser(RegisterRequestDTO userData) {
        // Log for debugging
        logger.info("Creating user with username: {}", userData.getUsername());

        // Validate the NIC
        if (!nicUtil.isValidNic(userData.getNic())) {
            throw new IllegalArgumentException("Invalid NIC format");
        }

        // Check if NIC is unique
        if (!nicUtil.isUniqueNic(userData.getNic())) {
            throw new IllegalArgumentException("NIC is already in use");
        }

        // Create a new UserEntity
        UserEntity newUser = new UserEntity();
        newUser.setUsername(userData.getUsername());
        newUser.setPassword(passwordEncoder.encode(userData.getPassword()));
        newUser.setEmail(userData.getEmail());
        newUser.setPhoneNo(formatNumber(userData.getPhoneNo()));
        newUser.setNic(userData.getNic());  // Set NIC

        // Save the user entity
        UserEntity savedUser = userRepository.save(newUser);

        // Log the saved user details
        logger.info("User created with ID: {}", savedUser.getId());

        // Return the saved user
        return savedUser;
    }
*/

   /* public LoginResponseDTO login(LoginRequestDTO loginData) {
        // Generate a unique identifier for this login attempt
        String referencesID = UUID.randomUUID().toString();



        logger.info("ref: {}", referencesID);

        // Existing login logic
        logger.info("Attempting login for user: {}", loginData.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword())
            );
        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for user: {} - In" +
                    "valid credentials", loginData.getUsername());
            return new LoginResponseDTO(null, null, "Invalid credentials", "The username or password is incorrect.", null);
        } catch (Exception e) {
            logger.error("Unexpected error during authentication for user {}: {}", loginData.getUsername(), e.getMessage(), e);
            return new LoginResponseDTO(null, null, "Authentication failed", "An unexpected error occurred during authentication.", null);
        }

        // Token generation logic
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", loginData.getUsername());
        claims.put("reference", referencesID);

        String accessToken = jwtService.getJWTToken(loginData.getUsername(), claims);
        String refreshToken = jwtService.getRefreshToken(loginData.getUsername(), claims);

        Optional<UserEntity> userEntity = userRepository.findByUsername(loginData.getUsername());

        if (userEntity.isEmpty()) {
            logger.info("No user found with this username{}", loginData.getUsername());
            return null;
        }

        UserEntity user = userEntity.get();
        user.setReferencesID(referencesID);
        user.setExpireAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        logger.info("Token generated successfully for user: {}", loginData.getUsername());
        return new LoginResponseDTO(accessToken, LocalDateTime.now(), null, "Token generated successfully", refreshToken);
    }*/



    public LoginResponseDTO login(LoginRequestDTO loginData) {
        String referencesID = UUID.randomUUID().toString();
        logger.info("ref: {}", referencesID);


        logger.info("Attempting login for user: {}", loginData.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword())
            );
        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for user: {} - Invalid credentials", loginData.getUsername());
            //throw new LoginFailedException("Invalid credentials: The username or password is incorrect.");
            throw new LoginFailedException(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during authentication for user {}: {}", loginData.getUsername(), e.getMessage(), e);
            throw new AuthenticationException("An unexpected error occurred during authentication.");
        }

        // Token generation logic
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", loginData.getUsername());
        claims.put("reference", referencesID);

        String accessToken = jwtService.getJWTToken(loginData.getUsername(), claims);
        String refreshToken = jwtService.getRefreshToken(loginData.getUsername(), claims);

        Optional<UserEntity> userEntity = userRepository.findByUsername(loginData.getUsername());

        if (userEntity.isEmpty()) {
            logger.info("No user found with this username {}", loginData.getUsername());
            throw new UserNotFoundException("User not found with username: " + loginData.getUsername());
        }

        UserEntity user = userEntity.get();
        user.setReferencesID(referencesID);
        user.setExpireAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        logger.info("Token generated successfully for user: {}", loginData.getUsername());
        return new LoginResponseDTO(accessToken, LocalDateTime.now(), null, "Token generated successfully", refreshToken);
    }



    /*public BaseResponse<HashMap<String, Object>> registerUser(RegisterRequestDTO registerData) {
        logger.info("Registering user with username: {}", registerData.getUsername());

        try {
            // Check if the user already exists
            if (isUserEnabled(registerData.getUsername())) {
                logger.warn("User already exists: {}", registerData.getUsername());
                return BaseResponse.<HashMap<String, Object>>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message(MessageConstantUtil.ALREADY_EXIST_ACCOUNT)
                        .build();
            }
            // Check if the email already exists
            else if (isEmailEnabled(registerData.getEmail())) {
                logger.warn("Email already exists: {}", registerData.getEmail());
                return BaseResponse.<HashMap<String, Object>>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message(MessageConstantUtil.ALREADY_HAS_EMAIL)
                        .build();
            }
            // Validate NIC number
            else if (!nicUtil.isValidNic(registerData.getNic())) {
                logger.warn("Invalid NIC number: {}", registerData.getNic());
                return BaseResponse.<HashMap<String, Object>>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message(MessageConstantUtil.INVALID_NIC)
                        .build();
            }
            else if (!nicUtil.isUniqueNic(registerData.getNic())) {
                logger.warn("Given NIC is already in the sytem: {}", registerData.getNic());
                return BaseResponse.<HashMap<String, Object>>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message(MessageConstantUtil.UNIQU_NIC)
                        .build();
            }

            else {
                PasswordValidator passwordValidator = new PasswordValidator();
                boolean isPasswordValid = passwordValidator.isValid(registerData.getPassword(), null);  // Passing null for the context
                logger.debug("Password validation result for '{}' is: {}", registerData.getPassword(), isPasswordValid);

                if (!isPasswordValid) {
                    logger.info("Given password does not meet the criteria.");
                    return BaseResponse.<HashMap<String, Object>>builder()
                            .code(ResponseUtil.FAILED_CODE)
                            .title(ResponseUtil.FAILED)
                            .message(MessageConstantUtil.INVALIED_PASSWORD_PATTERN)
                            .build();
                }
            }


            // Create user if all checks pass
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
*/


//    public RegisterResponseDTO register(RegisterRequestDTO registerData) {
//        logger.info("Registering user with username: {}", registerData.getUsername());
//
//        if (isUserEnabled(registerData.getUsername())) {
//            logger.warn("User already exists: {}", registerData.getUsername());
//            return new RegisterResponseDTO(null, "User already exists in the system");
//        }
//
//
//    }

    /*public LogoutRequestDTO logout(String accessToken) {

        Claims claims =jwtService.getTokenData(accessToken);
        String reference = claims.get("reference").toString();
        String username = claims.getSubject();

        if (!StringUtils.hasLength(reference)) {
            logger.warn("No reference ID found in the provided token.");
            return new LogoutRequestDTO("Invalid token or reference ID not found", null);
        }

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);

        if (userEntity.isEmpty()) {
            logger.warn("No user found with reference ID: {}", reference);
            return new LogoutRequestDTO("User not found or already logged out", null);
        }

        UserEntity user = userEntity.get();


        user.setReferencesID(null);
        userRepository.save(user);

        logger.info("User with reference ID: {} has been successfully logged out.", reference);

        // Optional: If you are maintaining a session store (like Redis), you can also remove the session here.
        // sessionStore.remove(referencesID);

        return new LogoutRequestDTO("Successfully logged out", null);
    }*/



    public void logout(String accessToken) {

        Claims claims = jwtService.getTokenData(accessToken);
        String reference = claims.get("reference").toString();
        String username = claims.getSubject();

        if (!StringUtils.hasLength(reference)) {
            logger.warn("No reference ID found in the provided token.");
            throw new RuntimeException("Invalid token or reference ID not found");
        }

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);

        if (userEntity.isEmpty()) {
            logger.warn("No user found with reference ID: {}", reference);
            throw new RuntimeException("User not found or already logged out");
        }





        UserEntity user = userEntity.get();
        user.setReferencesID(null);

        userRepository.save(user);

        logger.info("User with reference ID: {} has been successfully logged out.", reference);

        // Optional: If you are maintaining a session store (like Redis), you can also remove the session here.
        // sessionStore.remove(referencesID);
    }


    private boolean isUserEnabled(String username) {
        boolean isEnabled = userRepository.findByUsername(username).isPresent();
        logger.info("Is user '{}' enabled: {}", username, isEnabled);
        return isEnabled;
    }

    private boolean isEmailEnabled(String email) {
        boolean isEnabled = userRepository.findByEmail(email).isPresent();
        logger.info("Is user '{}' enabled: {}", email, isEnabled);
        return isEnabled;
    }












    

}