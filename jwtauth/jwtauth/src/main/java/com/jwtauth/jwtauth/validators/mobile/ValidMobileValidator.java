package com.jwtauth.jwtauth.validators.mobile;



import com.jwtauth.jwtauth.annotations.mobile.ValidMobile;
import com.jwtauth.jwtauth.utils.PhoneNumberUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidMobileValidator implements ConstraintValidator<ValidMobile, String> {

    private final PhoneNumberUtil phoneNumberUtil;

    @Override
    public void initialize(ValidMobile constraintAnnotation) {
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
        return phoneNumberUtil.isValidPhoneNumber(s);
    }
}

