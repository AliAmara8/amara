package com.ali.amara.auth.validation;

import com.ali.amara.auth.validation.annotations.EmailOrPhone;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailOrPhoneValidator implements ConstraintValidator<EmailOrPhone, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) {
            return false;
        }

        return EMAIL_VALIDATOR.isValid(value) || PHONE_PATTERN.matcher(value).matches();
    }
}