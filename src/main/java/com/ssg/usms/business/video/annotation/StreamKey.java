package com.ssg.usms.business.video.annotation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StreamKeyValidator.class)
public @interface StreamKey {

    String message() default "유효하지 않은 스트림 키 포맷입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
