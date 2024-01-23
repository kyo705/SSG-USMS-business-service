package com.ssg.usms.business.cctv.handler;

import com.ssg.usms.business.cctv.controller.CctvController;
import com.ssg.usms.business.cctv.exception.NotExistingCctvException;
import com.ssg.usms.business.cctv.exception.NotOwnedCctvException;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.store.exception.NotExistingStoreException;
import com.ssg.usms.business.store.exception.NotOwnedStoreException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

import static com.ssg.usms.business.constant.CustomStatusCode.*;

@Slf4j
@RestControllerAdvice(assignableTypes = CctvController.class)
public class CctvExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDto> handleBindException(BindException exception) {

        String errorMessage = exception.getFieldErrors().get(0).getDefaultMessage();

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", errorMessage);

        if(errorMessage.equals(INVALID_CCTV_NAME_FORMAT_MESSAGE)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDto(INVALID_CCTV_NAME_FORMAT_CODE, INVALID_CCTV_NAME_FORMAT_MESSAGE));
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException exception) {

        String errorMessage = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList())
                .get(0);

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", errorMessage);

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

    @ExceptionHandler({NotExistingCctvException.class})
    public ResponseEntity<ErrorResponseDto> handleNotExistingStoreException(NotExistingCctvException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace().length == 0 ? exception.getClass() : exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getErrorResponseDto());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorResponseDto());
    }

    @ExceptionHandler({NotOwnedCctvException.class})
    public ResponseEntity<ErrorResponseDto> handleNotOwnedStoreException(NotOwnedCctvException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace().length == 0 ? exception.getClass() : exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getErrorResponseDto());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorResponseDto());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAnyOtherException(Exception exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에서 에러가 발생했습니다."));
    }
}
