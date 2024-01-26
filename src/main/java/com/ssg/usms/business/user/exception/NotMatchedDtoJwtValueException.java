package com.ssg.usms.business.user.exception;

public class NotMatchedDtoJwtValueException extends IllegalFormException{

    public NotMatchedDtoJwtValueException(String message) {
        super(message);
    }
}
