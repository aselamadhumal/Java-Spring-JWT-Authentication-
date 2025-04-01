package com.jwtauth.jwtauth.validators.name;

import com.jwtauth.jwtauth.annotations.name.UniqueName;
import com.jwtauth.jwtauth.utils.NameUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {

    private final NameUtil nameUtil;

    @Override
    public void initialize(UniqueName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.trim().isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Username cannot be empty")
                    .addConstraintViolation();
            return false;
        }
        return isNameExistInDatabase(s);
    }

    private boolean isNameExistInDatabase(String name) {
        return nameUtil.isUniqueName(name);
    }
}

