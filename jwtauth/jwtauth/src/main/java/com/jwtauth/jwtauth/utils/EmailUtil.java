package com.jwtauth.jwtauth.utils;

import com.jwtauth.jwtauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailUtil {
    public static final String EMAIL_PATTERN = "^(.+)@(.+)$";

    private final UserRepository userRepository;

   public boolean isValidEmail(String mobile) {
        return mobile.matches(EMAIL_PATTERN);
    }

    public boolean isUniqueEmail(String email) {
        boolean isCustomerExists = userRepository.existsUserByEmail(email);
        if (isCustomerExists) {
            log.warn("isUniqueEmail-> email: {} already exists", email);
            return false;
        }
        log.info("isUniqueEmail-> email: {} verified", email);
        return true;
    }
}
