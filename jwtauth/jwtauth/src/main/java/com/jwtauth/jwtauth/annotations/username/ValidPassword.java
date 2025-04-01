package com.jwtauth.jwtauth.annotations.username;

import com.jwtauth.jwtauth.validators.password.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password must be at least 8 characters, include a digit, an uppercase letter, and a special character";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

