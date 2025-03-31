package com.jwtauth.jwtauth.validators.nic;



import com.jwtauth.jwtauth.annotations.nic.ValidNic;
import com.jwtauth.jwtauth.utils.NicUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidNicValidator implements ConstraintValidator<ValidNic, String> {

    private final NicUtil nicUtil;

    @Override
    public void initialize(ValidNic constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.trim().isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("nic cannot be empty")
                    .addConstraintViolation();
            return false;
        }
        return nicUtil.isValidNic(s);
    }
}

