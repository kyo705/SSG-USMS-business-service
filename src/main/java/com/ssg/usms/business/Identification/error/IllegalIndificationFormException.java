package com.ssg.usms.business.Identification.error;

import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.Getter;

@Getter
public abstract class IllegalIndificationFormException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto;

    public IllegalIndificationFormException(String message){

        errorResponseDto = new ErrorResponseDto(400, message);
    }

    public IllegalIndificationFormException(int code, String message){

        errorResponseDto = new ErrorResponseDto(code, message);
    }



    @Override
    public String getMessage(){

        return errorResponseDto.getMessage();
    }


}