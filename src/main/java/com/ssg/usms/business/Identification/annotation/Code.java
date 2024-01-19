package com.ssg.usms.business.Identification.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.ssg.usms.business.Identification.constant.IdenticationConstant.NOT_ALLOWED_VERIFICATION_CODE_LITERAL;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CodeValidator.class)
public @interface Code {

    String message() default NOT_ALLOWED_VERIFICATION_CODE_LITERAL;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
