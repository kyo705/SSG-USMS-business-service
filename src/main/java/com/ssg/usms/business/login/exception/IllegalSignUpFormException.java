package com.ssg.usms.business.login.exception;


import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.Getter;

@Getter
public abstract class IllegalSignUpFormException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto;

    public IllegalSignUpFormException(String message){

        errorResponseDto = new ErrorResponseDto(400, message);
    }

    public IllegalSignUpFormException(int code, String message){

        errorResponseDto = new ErrorResponseDto(code, message);
    }


    @Override
    public String getMessage(){

        return errorResponseDto.getMessage();
    }


}
