package com.ssg.usms.business.store.exception;

import lombok.Getter;

@Getter
public class UnexpectedImageSavingException extends RuntimeException {

    private String message;

    public UnexpectedImageSavingException(String message) {
        this.message = message;
    }
}
