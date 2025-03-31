package com.jwtauth.jwtauth.annotations.mobile;
import com.jwtauth.jwtauth.validators.mobile.UniqueMobileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;



import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueMobileValidator.class)
@Documented
public @interface UniqueMobile {
    String message() default "Phone number already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

