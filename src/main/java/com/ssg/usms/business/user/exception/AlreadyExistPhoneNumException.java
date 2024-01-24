package com.ssg.usms.business.user.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.ALREADY_EXIST_PHONE_NUM;

public class AlreadyExistPhoneNumException extends IllegalFormException {

    public AlreadyExistPhoneNumException(String message){

        super(ALREADY_EXIST_PHONE_NUM,message);
    }
}
