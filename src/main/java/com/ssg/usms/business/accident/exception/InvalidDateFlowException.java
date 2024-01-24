package com.ssg.usms.business.accident.exception;

import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.Getter;

import static com.ssg.usms.business.constant.CustomStatusCode.INVALID_DATE_FORMAT_CODE;
import static com.ssg.usms.business.constant.CustomStatusCode.INVALID_DATE_FORMAT_MESSAGE;

@Getter
public class InvalidDateFlowException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto = new ErrorResponseDto(INVALID_DATE_FORMAT_CODE, INVALID_DATE_FORMAT_MESSAGE);
}
