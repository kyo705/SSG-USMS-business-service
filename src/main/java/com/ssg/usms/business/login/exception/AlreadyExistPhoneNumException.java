package com.ssg.usms.business.login.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.ALREADY_EXIST_PHONE_NUM;

public class AlreadyExistPhoneNumException extends IllegalSignUpFormException{

    public AlreadyExistPhoneNumException(String message){

        super(ALREADY_EXIST_PHONE_NUM,message);
    }
}
