package com.ssg.usms.business.User.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_ALLOWED_ID_FORM;

public class NotAllowedIdFormException extends IllegalSignUpFormException{
    public NotAllowedIdFormException(String message){

        super(NOT_ALLOWED_ID_FORM, message);
    }
}
