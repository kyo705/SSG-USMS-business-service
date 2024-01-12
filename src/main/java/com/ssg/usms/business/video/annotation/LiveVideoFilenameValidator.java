package com.ssg.usms.business.video.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

import static com.ssg.usms.business.video.constant.VideoConstants.LIVE_VIDEO_FILE_NAME_PATTERN;

public class LiveVideoFilenameValidator implements ConstraintValidator<LiveVideoFilename, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(value == null) return true;

        return Pattern.matches(LIVE_VIDEO_FILE_NAME_PATTERN, value);
    }
}
