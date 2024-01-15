package com.ssg.usms.business.User.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_ALLOWED_PHONENUMBER_FORM;

public class NotAllowedPhoneNumberFormException extends IllegalSignUpFormException{

    public NotAllowedPhoneNumberFormException(String message){

        super(NOT_ALLOWED_PHONENUMBER_FORM,message);
    }
}
