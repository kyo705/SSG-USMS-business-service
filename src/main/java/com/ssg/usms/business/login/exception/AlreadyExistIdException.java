package com.ssg.usms.business.login.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.ALREADY_EXIST_ID;

public class AlreadyExistIdException extends IllegalSignUpFormException{

    public AlreadyExistIdException(String message){

        super(ALREADY_EXIST_ID, message);
    }
}
