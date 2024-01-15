package com.ssg.usms.business.User.annotation;

import com.ssg.usms.business.User.exception.NotAllowedPhoneNumberFormException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<CustomPhoneNumber, String> {
    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {


        if (!isValidPhoneNumber(phoneNumber)) {
            throw new NotAllowedPhoneNumberFormException("비밀 번호의 길이는 최소 8자 이상 최대 30자 이하이고 문자, 숫자, 특수문자가 적어도 하나 이상 포함되어야 합니다.");
        }

        return true;
    }




    public static boolean isValidPhoneNumber(String phoneNumber) {

        String phoneRegex = "\\d{3}-\\d{4}-\\d{4}";

        return phoneNumber.matches(phoneRegex);
    }
}
