package com.ssg.usms.business.video.annotation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.ssg.usms.business.constant.CustomStatusCode.INVALID_STREAM_KEY_FORMAT_MESSAGE;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StreamKeyValidator.class)
public @interface StreamKey {

    String message() default INVALID_STREAM_KEY_FORMAT_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
