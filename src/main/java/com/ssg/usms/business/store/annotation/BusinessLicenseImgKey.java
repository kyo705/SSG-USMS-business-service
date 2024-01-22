package com.ssg.usms.business.store.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.ssg.usms.business.constant.CustomStatusCode.INVALID_BUSINESS_LICENSE_IMG_KEY_FORMAT_MESSAGE;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BusinessLicenseImgKeyValidator.class)
public @interface BusinessLicenseImgKey {

    String message() default INVALID_BUSINESS_LICENSE_IMG_KEY_FORMAT_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
