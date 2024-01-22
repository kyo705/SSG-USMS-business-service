package com.ssg.usms.business.user.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.ssg.usms.business.user.constant.UserConstants.NICKNAME_PATTERN;

public class NickNameValidator implements ConstraintValidator<NickName, String> {
    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {

        if (!isValidNickName(nickname)) {
            return false;
        }

        return true;
    }


    private boolean isValidNickName(String nickname) {

        return nickname.matches(NICKNAME_PATTERN);
    }
}
