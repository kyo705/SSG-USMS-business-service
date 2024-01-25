package com.ssg.usms.business.user.exception;

import org.springframework.http.HttpStatus;


public class NotAllowedFormCheckException extends IllegalFormException{
    public NotAllowedFormCheckException(String message){

        super(HttpStatus.CONFLICT.value(), message);
    }
}
