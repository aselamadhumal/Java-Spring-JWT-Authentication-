package com.jwtauth.jwtauth.annotations.email;

import com.jwtauth.jwtauth.validators.UniqueEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
@Documented
public @interface UniqueEmail {
    String message() default "Email address already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
