package com.jwtauth.jwtauth.validators.mobile;

import com.jwtauth.jwtauth.annotations.mobile.UniqueMobile;
import com.jwtauth.jwtauth.utils.PhoneNumberUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueMobileValidator implements ConstraintValidator<UniqueMobile, String> {

    private final PhoneNumberUtil phoneNumberUtil;

    @Override
    public void initialize(UniqueMobile constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.trim().isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Phone number cannot be empty")
                    .addConstraintViolation();
            return false;
        }
        return isPhoneExistsInDatabase(s);
    }

    private boolean isPhoneExistsInDatabase(String phoneNumber) {
        return phoneNumberUtil.isUniquePhoneNumber(phoneNumber);
    }
}
