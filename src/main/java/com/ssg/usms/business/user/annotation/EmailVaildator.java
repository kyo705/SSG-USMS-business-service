package com.ssg.usms.business.user.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.ssg.usms.business.user.constant.UserConstants.EMAIL_PATTERN;

public class EmailVaildator implements ConstraintValidator<Email, String> {
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {

        if (!isValidEmail(email)) {
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String Email){

        return Email.matches(EMAIL_PATTERN);
    }

}




