package com.jwtauth.jwtauth.annotations.nic;


import com.jwtauth.jwtauth.validators.nic.UniqueNicValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;


import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNicValidator.class)
@Documented
public @interface UniqueNic {
    String message() default "NIC number already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

