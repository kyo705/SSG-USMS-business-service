package com.ssg.usms.business.store.exception;

import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.Getter;

import static com.ssg.usms.business.constant.CustomStatusCode.UNAVAILABLE_STORE_CODE;
import static com.ssg.usms.business.constant.CustomStatusCode.UNAVAILABLE_STORE_MESSAGE;

@Getter
public class UnavailableStoreException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto = new ErrorResponseDto(UNAVAILABLE_STORE_CODE, UNAVAILABLE_STORE_MESSAGE);
}
