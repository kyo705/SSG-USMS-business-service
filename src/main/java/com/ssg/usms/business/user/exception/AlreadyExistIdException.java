package com.ssg.usms.business.user.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.ALREADY_EXIST_USERNAME;

public class AlreadyExistIdException extends IllegalSignUpFormException{

    public AlreadyExistIdException(String message){

        super(ALREADY_EXIST_USERNAME, message);
    }
}