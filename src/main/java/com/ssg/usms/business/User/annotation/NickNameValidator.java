package com.ssg.usms.business.User.annotation;

import com.ssg.usms.business.User.exception.NotAllowedNickNameFormException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NickNameValidator implements ConstraintValidator<CustomeNickName, String> {
    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {


        if (isValidNickName(nickname)) {
            throw new NotAllowedNickNameFormException("닉네임은 최소 1자이상 10자이하 특수문자가 포함되어서는 안됩니다.");
        }

        return true;
    }


    private boolean isValidNickName(String nickname) {

        String specialCharacterPattern = "[^a-zA-Z0-9]";
        Pattern pattern = Pattern.compile(specialCharacterPattern);
        Matcher matcher = pattern.matcher(nickname);

        return matcher.find();
    }
}
