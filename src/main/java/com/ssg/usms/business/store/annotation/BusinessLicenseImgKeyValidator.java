package com.ssg.usms.business.store.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

import static com.ssg.usms.business.store.constant.StoreConstants.BUSINESS_LICENSE_IMG_KEY_REGEX;

public class BusinessLicenseImgKeyValidator implements ConstraintValidator<BusinessLicenseImgKey, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(value == null) return true;

        return Pattern.matches(BUSINESS_LICENSE_IMG_KEY_REGEX, value);
    }
}
