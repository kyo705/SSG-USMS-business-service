package com.ssg.usms.business.user.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.ssg.usms.business.user.constant.UserConstants.USERNAME_PATTERN;

public class usernameValidator implements ConstraintValidator<UserName, String> {
    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {

        if (!isValidUserName(username)) {
            return false;
        }
        return true;
    }

    private boolean isValidUserName(String username) {

        return username.matches(USERNAME_PATTERN);
    }
}
