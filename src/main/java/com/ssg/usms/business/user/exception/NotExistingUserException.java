package com.ssg.usms.business.user.exception;

import org.springframework.http.HttpStatus;

public class NotExistingUserException extends IllegalFormException{
    public NotExistingUserException(String message){

        super(HttpStatus.BAD_REQUEST.value(),message);
    }
}
