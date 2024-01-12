package com.ssg.usms.business.video.exception;

import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.Getter;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_ALLOWED_STREAM_PROTOCOL;

@Getter
public class NotAllowedStreamingProtocolException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto;

    public NotAllowedStreamingProtocolException(String message) {

        errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_STREAM_PROTOCOL, message);
    }

    @Override
    public String getMessage() {

        return errorResponseDto.getMessage();
    }
}
