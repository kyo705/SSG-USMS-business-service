package com.ssg.usms.business.user.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SecurityStateValidator.class)
public @interface SecurityStateValid {

    String message() default "허용되지 않은 보안 레벨 코드입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
