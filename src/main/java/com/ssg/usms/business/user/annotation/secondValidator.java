package com.ssg.usms.business.user.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.ssg.usms.business.user.constant.UserConstants.SECONDPASSWORD_PATTERN;

public class secondValidator implements ConstraintValidator<SecondPassword, String> {

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {

        if (!isValidsecondPassword(nickname)) {
            return false;
        }

        return true;
    }


    private boolean isValidsecondPassword(String secondpassword) {

        return secondpassword.matches(SECONDPASSWORD_PATTERN);
    }
}
