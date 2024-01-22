package com.ssg.usms.business.user.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.ssg.usms.business.user.constant.UserConstants.PASSWORD_PATTERN;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        if (!isValidPassword(password)) {
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {

        return password.matches(PASSWORD_PATTERN);
    }
}
