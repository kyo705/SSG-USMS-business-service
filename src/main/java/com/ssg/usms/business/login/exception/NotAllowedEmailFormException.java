package com.ssg.usms.business.login.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_ALLOWED_EMAIL_FORM;


public class NotAllowedEmailFormException extends IllegalSignUpFormException{

    public NotAllowedEmailFormException(String message){

        super(NOT_ALLOWED_EMAIL_FORM, message);
    }
}
