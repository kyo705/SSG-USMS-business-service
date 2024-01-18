package com.ssg.usms.business.Identification.error;

import org.springframework.http.HttpStatus;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_ALLOWED_KEY;

public class NotIdentificationException extends IllegalIndificationFormException{
    public NotIdentificationException(String message){

        super(HttpStatus.BAD_REQUEST.value(), message);
    }
}
