package com.ssg.usms.business.Identification.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CodeValidator implements ConstraintValidator<Code, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return (value == 0 || value == 1);
    }
}
