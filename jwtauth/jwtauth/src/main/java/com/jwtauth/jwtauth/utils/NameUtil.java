package com.jwtauth.jwtauth.utils;
import com.jwtauth.jwtauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NameUtil {
    private final UserRepository userRepository;

    public boolean isUniqueName(String name) {
        boolean isUserEnteredNameExists = isUsernameExists(name);
        if (isUserEnteredNameExists) {
            log.warn("isUniqueName -> Name already exists, name : {}", name);
            return false;
        } else {
            log.info("isUniqueName-> Name is not on the db");
            return true;
        }
    }

    public boolean isUsernameExists(String name) {
        return userRepository.existsByUsername(name);
    }
}
