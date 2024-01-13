package com.ssg.usms.business.video.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

import static com.ssg.usms.business.video.constant.VideoConstants.REPLAY_VIDEO_FILE_NAME_PATTERN;

public class ReplayVideoFilenameValidator implements ConstraintValidator<ReplayVideoFilename, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(value == null) return true;

        return Pattern.matches(REPLAY_VIDEO_FILE_NAME_PATTERN, value);
    }
}
