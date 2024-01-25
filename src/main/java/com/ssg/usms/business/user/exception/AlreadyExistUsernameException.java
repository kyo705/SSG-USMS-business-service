package com.ssg.usms.business.user.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.ALREADY_EXIST_USERNAME;

public class AlreadyExistUsernameException extends IllegalFormException {

    public AlreadyExistUsernameException(String message){

        super(ALREADY_EXIST_USERNAME, message);
    }
}
