package com.jwtauth.jwtauth.annotations.nic;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import com.jwtauth.jwtauth.validators.nic.ValidNicValidator;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidNicValidator.class)
@Documented
public @interface ValidNic {
    String message() default "Please provide a valid NIC";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

