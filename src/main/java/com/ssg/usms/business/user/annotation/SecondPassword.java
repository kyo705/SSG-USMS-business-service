package com.ssg.usms.business.user.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = secondValidator.class)
public @interface SecondPassword {
    String message() default "유효하지 않은 2차비밀번호 형식입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
