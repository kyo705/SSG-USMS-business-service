package com.ssg.usms.business.user.exception;

import org.springframework.http.HttpStatus;

public class NotAllowedSecondPasswordException extends IllegalFormException {

    public NotAllowedSecondPasswordException(String message){

        super(HttpStatus.BAD_REQUEST.value(), message);
    }
}
