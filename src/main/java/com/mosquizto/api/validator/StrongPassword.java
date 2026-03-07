package com.mosquizto.api.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {

    String message() default "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
