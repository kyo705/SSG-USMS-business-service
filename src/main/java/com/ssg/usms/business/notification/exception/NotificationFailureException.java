package com.ssg.usms.business.notification.exception;


import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.Getter;

@Getter
public class NotificationFailureException extends RuntimeException{

    private final ErrorResponseDto errorResponseDto;

    public NotificationFailureException(String message){

        errorResponseDto = new ErrorResponseDto(500, message);
    }

    public NotificationFailureException(int code, String message){

        errorResponseDto = new ErrorResponseDto(code, message);
    }

    @Override
    public String getMessage(){

        return errorResponseDto.getMessage();
    }
}
