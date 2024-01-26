package com.ssg.usms.business.user.annotation;

import com.ssg.usms.business.user.dto.SecurityState;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SecurityStateValidator implements ConstraintValidator<SecurityStateValid, SecurityState> {

    @Override
    public boolean isValid(SecurityState code, ConstraintValidatorContext context) {

        if (code == SecurityState.BASIC || code == SecurityState.SMS || code == SecurityState.SECONDPASSWORD) {
            return true;
        }
        return false;
    }

}
