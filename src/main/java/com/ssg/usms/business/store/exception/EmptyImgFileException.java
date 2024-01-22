package com.ssg.usms.business.store.exception;

import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.Getter;

import static com.ssg.usms.business.constant.CustomStatusCode.EMPTY_IMG_FILE_CODE;
import static com.ssg.usms.business.constant.CustomStatusCode.EMPTY_IMG_FILE_MESSAGE;

@Getter
public class EmptyImgFileException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto =
            new ErrorResponseDto(EMPTY_IMG_FILE_CODE, EMPTY_IMG_FILE_MESSAGE);

}
