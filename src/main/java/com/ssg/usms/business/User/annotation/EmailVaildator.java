package com.ssg.usms.business.User.annotation;

import com.ssg.usms.business.User.exception.NotAllowedEmailFormException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailVaildator implements ConstraintValidator<CustomEmail, String> {
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {

        if (!isValidEmail(email)) {
            throw new NotAllowedEmailFormException("잘못된 이메일 양식입니다.");
        }

        return true;
    }

    private boolean isValidEmail(String Email){

        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(Email);

        return matcher.matches();
    }

}




