package com.ssg.usms.business.User.annotation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailVaildator.class)
public @interface CustomEmail {

    String message() default "유효하지 않은 이메일 형식입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}



