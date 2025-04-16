package com.jwtauth.jwtauth.validators;

import com.jwtauth.jwtauth.annotations.email.UniqueEmail;
import com.jwtauth.jwtauth.utils.EmailUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private EmailUtil emailUtil;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Email cannot be empty")
                    .addConstraintViolation();
            return false;
        }
        return emailUtil.isUniqueEmail(email);
    }
}
