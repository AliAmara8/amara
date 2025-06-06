package com.ali.amara.auth.validation.annotations;

import com.ali.amara.auth.validation.EmailOrPhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailOrPhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailOrPhone {
    String message() default "Must be a valid email address or phone number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}