package com.ssg.usms.business.User.annotation;

import com.ssg.usms.business.User.exception.NotAllowedIdFormException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class usernameValidator implements ConstraintValidator<CustomUsername, String> {
    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {

        if (!isValidUserName(username)) {
            throw new NotAllowedIdFormException("아이디의 길이는 최소 5자 이상 최대 20자 이하, 특수문자가 포함되어서는 안됩니다.");
        }
        return true;
    }

    private boolean containsSpecialCharacter(String str) {
        // 정규식
        String specialCharacterPattern = "[^a-zA-Z0-9]";
        Pattern pattern = Pattern.compile(specialCharacterPattern);
        Matcher matcher = pattern.matcher(str);

        // 특수 문자가 포함되면 true 반환
        return matcher.find();
    }


    private boolean isValidUserName(String username) {

        int UsernameLength = username.length();
        return (UsernameLength >= 5 && UsernameLength <= 20 && !containsSpecialCharacter(username));
    }
}
