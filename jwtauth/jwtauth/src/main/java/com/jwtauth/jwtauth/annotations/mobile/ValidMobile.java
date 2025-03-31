package com.jwtauth.jwtauth.annotations.mobile;
import com.jwtauth.jwtauth.validators.mobile.ValidMobileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;


import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidMobileValidator.class)
@Documented
public @interface ValidMobile {
    String message() default "Please provide a valid phone number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

