package com.ssg.usms.business.User.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_ALLOWED_PASSWORD_FORM;

public class NotAllowedPassWordFormException extends IllegalSignUpFormException{
    public NotAllowedPassWordFormException(String message){

        super(NOT_ALLOWED_PASSWORD_FORM,message);
    }
}
