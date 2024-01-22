package com.ssg.usms.business.user.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.ssg.usms.business.user.constant.UserConstants.PHONENUMBER_PATTERN;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {


        if (!isValidPhoneNumber(phoneNumber)) {
            return false;
        }

        return true;
    }




    public static boolean isValidPhoneNumber(String phoneNumber) {

        return phoneNumber.matches(PHONENUMBER_PATTERN);
    }
}
