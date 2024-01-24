package com.ssg.usms.business.accident.exception;

import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.Getter;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_EXISTING_ACCIDENT_BEHAVIOR_CODE;
import static com.ssg.usms.business.constant.CustomStatusCode.NOT_EXISTING_ACCIDENT_BEHAVIOR_MESSAGE;

@Getter
public class NotExistingAccidentBehaviorException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_EXISTING_ACCIDENT_BEHAVIOR_CODE, NOT_EXISTING_ACCIDENT_BEHAVIOR_MESSAGE);
}
