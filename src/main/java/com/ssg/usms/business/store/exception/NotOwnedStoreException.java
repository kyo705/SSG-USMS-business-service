package com.ssg.usms.business.store.exception;

import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.Getter;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_OWNED_STORE_CODE;
import static com.ssg.usms.business.constant.CustomStatusCode.NOT_OWNED_STORE_MESSAGE;

@Getter
public class NotOwnedStoreException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto =
            new ErrorResponseDto(NOT_OWNED_STORE_CODE, NOT_OWNED_STORE_MESSAGE);

}
