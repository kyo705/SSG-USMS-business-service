package com.ssg.usms.business.warning.handler;

import com.ssg.usms.business.accident.exception.InvalidDateFlowException;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.store.exception.NotExistingStoreException;
import com.ssg.usms.business.store.exception.NotOwnedStoreException;
import com.ssg.usms.business.store.exception.UnavailableStoreException;
import com.ssg.usms.business.warning.controller.RegionWarningController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@ControllerAdvice(assignableTypes = RegionWarningController.class)
public class RegionWarningExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDto> handleBindException(BindException exception) {

        String errorMessage = exception.getFieldErrors().get(0).getDefaultMessage();

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", errorMessage);

        if(errorMessage.equals(INVALID_DATE_FORMAT_MESSAGE)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDto(INVALID_DATE_FORMAT_CODE, INVALID_DATE_FORMAT_MESSAGE));
        }
        if(errorMessage.equals(NOT_ALLOWED_PAGE_OFFSET_FORMAT_MESSAGE)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDto(NOT_ALLOWED_PAGE_OFFSET_FORMAT_CODE, NOT_ALLOWED_PAGE_OFFSET_FORMAT_MESSAGE));
        }
        if(errorMessage.equals(NOT_ALLOWED_PAGE_SIZE_FORMAT_MESSAGE)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDto(NOT_ALLOWED_PAGE_SIZE_FORMAT_CODE, NOT_ALLOWED_PAGE_SIZE_FORMAT_MESSAGE));
        }
        throw new IllegalStateException();
    }

    @ExceptionHandler(InvalidDateFlowException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidDateFlowException(InvalidDateFlowException exception) {

        String errorMessage = exception.getMessage();

        log.error("Exception [Err_Location] : {}", exception.getClass());
        log.error("Exception [Err_Msg] : {}", errorMessage);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorResponseDto());

    }

    @ExceptionHandler({UnavailableStoreException.class})
    public ResponseEntity<ErrorResponseDto> handleUnavailableStoreException(UnavailableStoreException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace().length == 0 ? exception.getClass() : exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getErrorResponseDto());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorResponseDto());
    }

    @ExceptionHandler({NotExistingStoreException.class})
    public ResponseEntity<ErrorResponseDto> handleNotExistingStoreException(NotExistingStoreException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace().length == 0 ? exception.getClass() : exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getErrorResponseDto());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorResponseDto());
    }

    @ExceptionHandler({NotOwnedStoreException.class})
    public ResponseEntity<ErrorResponseDto> handleNotOwnedStoreException(NotOwnedStoreException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace().length == 0 ? exception.getClass() : exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getErrorResponseDto());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorResponseDto());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAnyOtherException(Exception exception) {

        log.error(exception.getClass().toString());
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(500, "내부 에러 발생"));
    }
}
