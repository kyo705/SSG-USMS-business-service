package com.ssg.usms.business.store.exception;

import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.Getter;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_ALLOWED_IMG_FILE_FORMAT_CODE;
import static com.ssg.usms.business.constant.CustomStatusCode.NOT_ALLOWED_IMG_FILE_FORMAT_MESSAGE;

@Getter
public class NotAllowedImgFileFormatException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto =
            new ErrorResponseDto(NOT_ALLOWED_IMG_FILE_FORMAT_CODE, NOT_ALLOWED_IMG_FILE_FORMAT_MESSAGE);

}
