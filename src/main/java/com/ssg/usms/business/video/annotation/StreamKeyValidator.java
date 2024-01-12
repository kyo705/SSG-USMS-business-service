package com.ssg.usms.business.video.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

import static com.ssg.usms.business.video.constant.VideoConstants.STREAM_KEY_PATTERN;

public class StreamKeyValidator  implements ConstraintValidator<StreamKey, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(value == null) return true;

        return Pattern.matches(STREAM_KEY_PATTERN, value);
    }
}
