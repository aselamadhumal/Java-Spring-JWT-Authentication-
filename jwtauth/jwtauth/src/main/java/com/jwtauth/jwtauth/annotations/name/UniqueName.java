package com.jwtauth.jwtauth.annotations.name;

import com.jwtauth.jwtauth.validators.name.UniqueNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNameValidator.class)
@Documented
public @interface UniqueName {
    String message() default "Username already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

