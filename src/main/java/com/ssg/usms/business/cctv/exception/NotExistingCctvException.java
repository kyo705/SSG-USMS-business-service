package com.ssg.usms.business.cctv.exception;

import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.Getter;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_EXISTING_CCTV_CODE;
import static com.ssg.usms.business.constant.CustomStatusCode.NOT_EXISTING_CCTV_MESSAGE;

@Getter
public class NotExistingCctvException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto =
            new ErrorResponseDto(NOT_EXISTING_CCTV_CODE, NOT_EXISTING_CCTV_MESSAGE);

}
