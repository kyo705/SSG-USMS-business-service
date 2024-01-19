package com.ssg.usms.business.Identification.error;

import org.springframework.http.HttpStatus;

public class NotIdentificationException extends IllegalIndificationFormException{
    public NotIdentificationException(String message){

        super(HttpStatus.BAD_REQUEST.value(), message);
    }
}
