package com.ssg.usms.business.video.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReplayVideoFilenameValidator.class)
public @interface ReplayVideoFilename {

    String message() default "유효하지 않은 리플레이 스트리밍 비디오 파일 명입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
