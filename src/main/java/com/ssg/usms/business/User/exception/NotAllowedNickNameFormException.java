package com.ssg.usms.business.User.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_ALLOWED_NICKNAME_FORM;

public class NotAllowedNickNameFormException extends IllegalSignUpFormException{

    public NotAllowedNickNameFormException(String message){

        super(NOT_ALLOWED_NICKNAME_FORM,message);
    }
}
