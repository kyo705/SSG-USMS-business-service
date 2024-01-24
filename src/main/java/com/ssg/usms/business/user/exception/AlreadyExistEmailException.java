package com.ssg.usms.business.user.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistEmailException extends IllegalFormException {

    public AlreadyExistEmailException(String message){

        super(HttpStatus.CONFLICT.value(),message);
    }
}
