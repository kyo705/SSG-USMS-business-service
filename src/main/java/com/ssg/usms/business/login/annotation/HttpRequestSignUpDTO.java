package com.ssg.usms.business.login.annotation;


import com.ssg.usms.business.video.annotation.StreamKeyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HttpRequestSignUpdtoValidator.class)
public @interface HttpRequestSignUpDTO {

    String message() default "유효하지 않은 dto 포맷입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
