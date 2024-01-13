package com.ssg.usms.business.login.annotation;

import com.ssg.usms.business.login.exception.NotAllowedEmailFormException;
import com.ssg.usms.business.login.exception.NotAllowedIdFormException;
import com.ssg.usms.business.login.exception.NotAllowedPassWordFormException;
import com.ssg.usms.business.login.persistence.HttpRequestSignUpDto;
import org.springframework.security.core.userdetails.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequestSignUpdtoValidator implements ConstraintValidator<HttpRequestSignUpDTO, HttpRequestSignUpDto> {
    @Override
    public boolean isValid(HttpRequestSignUpDto dto, ConstraintValidatorContext context) {

        if (!isValidPassword(dto.getPassword())) {
            throw new NotAllowedPassWordFormException("비밀 번호의 길이는 최소 8자 이상 최대 30자 이하이고 문자, 숫자, 특수문자가 적어도 하나 이상 포함되어야 합니다.");
        }

        if (!isValidUserName(dto.getUsername())) {
            throw new NotAllowedIdFormException("아이디의 길이는 최소 5자 이상 최대 20자 이하, 특수문자가 포함되어서는 안됩니다.");
        }

        if (!isValidEmail(dto.getEmail())) {
            throw new NotAllowedEmailFormException("잘못된 이메일 양식입니다.");
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
        System.out.println(UsernameLength);
        return (UsernameLength >= 5 && UsernameLength <= 20 && !containsSpecialCharacter(username));
    }
    private boolean isValidEmail(String Email){

        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(Email);

        return matcher.matches();
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
