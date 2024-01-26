package com.ssg.usms.business.user.exception;


import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.Getter;

@Getter
public abstract class IllegalFormException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto;

    public IllegalFormException(String message){

        errorResponseDto = new ErrorResponseDto(400, message);
    }

    public IllegalFormException(int code, String message){

        errorResponseDto = new ErrorResponseDto(code, message);
    }



    @Override
    public String getMessage(){

        return errorResponseDto.getMessage();
    }


}
