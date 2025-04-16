package com.jwtauth.jwtauth.utils;

import com.jwtauth.jwtauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhoneNumberUtil {


    private static final String PHONE_NUMBER_PATTERN = "^94[0-9]{9}$";

    private final UserRepository userRepository;


    public boolean isValidPhoneNumber(String phoneNumber) {
        String formattedPhoneNumber = formatNumber(phoneNumber);
        return formattedPhoneNumber.matches(PHONE_NUMBER_PATTERN);
    }

    public boolean isUniquePhoneNumber(String phoneNumber) {
        String formattedPhoneNumber = formatNumber(phoneNumber);
        boolean isCustomerExists = userRepository.existsUserEntityByPhoneNo(formattedPhoneNumber);
        if (isCustomerExists) {
            log.warn("isUniquePhoneNumber-> phone: {} already exists", formattedPhoneNumber);
            return false;  // Phone number already exists
        }
        log.info("isUniquePhoneNumber-> phone: {} verified", formattedPhoneNumber);
        return true;  // Phone number is unique
    }

    public static String formatNumber(String mobile) {

        mobile = mobile.trim();

        if (mobile.charAt(0) == '0' && mobile.length() == 10) {
            mobile = mobile.substring(1);
        }

        final String firstTwoDigits = mobile.substring(0, 2);
        if (!firstTwoDigits.equals("94")) {
            mobile = "94" + mobile;
        }

        return mobile;
    }
}
