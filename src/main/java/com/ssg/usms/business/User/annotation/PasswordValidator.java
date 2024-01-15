package com.ssg.usms.business.User.annotation;

import com.ssg.usms.business.User.exception.NotAllowedPassWordFormException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<CustomPassword, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {


        if (!isValidPassword(password)) {
            throw new NotAllowedPassWordFormException("비밀 번호의 길이는 최소 8자 이상 최대 30자 이하이고 문자, 숫자, 특수문자가 적어도 하나 이상 포함되어야 합니다.");
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


    private boolean isValidPassword(String password) {

        int passwordLength = password.length();
//      비밀번호 길이는 최소 8 최대 30
        if( (passwordLength > 8 && passwordLength <= 30) ){

            boolean hasLetter = false;
            boolean hasDigit = false;
            boolean hasSpecialChar = false;

            for (char ch : password.toCharArray()) {
                if (Character.isLetter(ch)) {
                    hasLetter = true;
                } else if (Character.isDigit(ch)) {
                    hasDigit = true;
                }
            }

            if (containsSpecialCharacter(password)){
                hasSpecialChar =true;
            }

            return hasLetter && hasDigit && hasSpecialChar;
        }

        return false;
    }
}
