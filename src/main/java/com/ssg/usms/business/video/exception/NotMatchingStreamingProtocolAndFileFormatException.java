package com.ssg.usms.business.video.exception;

import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.Getter;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_MATCHING_STREAM_PROTOCOL_AND_FILE_FORMAT;

@Getter
public class NotMatchingStreamingProtocolAndFileFormatException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto;

    public NotMatchingStreamingProtocolAndFileFormatException(String message) {

        errorResponseDto = new ErrorResponseDto(NOT_MATCHING_STREAM_PROTOCOL_AND_FILE_FORMAT, message);
    }

    @Override
    public String getMessage() {

        return errorResponseDto.getMessage();
    }
}
