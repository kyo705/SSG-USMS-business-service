package com.ssg.usms.business.store.handler;

import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.store.controller.StoreController;
import com.ssg.usms.business.store.exception.EmptyImgFileException;
import com.ssg.usms.business.store.exception.NotAllowedImgFileFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.ssg.usms.business.constant.CustomStatusCode.*;

@Slf4j
@RestControllerAdvice(assignableTypes = StoreController.class)
public class StoreExceptionHandler {

    @ExceptionHandler(EmptyImgFileException.class)
    public ResponseEntity<ErrorResponseDto> handleEmptyImgFileException(EmptyImgFileException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorResponseDto());
    }

    @ExceptionHandler(NotAllowedImgFileFormatException.class)
    public ResponseEntity<ErrorResponseDto> handleNotAllowedImgFileFormatException(NotAllowedImgFileFormatException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorResponseDto());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDto> handleBindException(BindException exception) {

        String errorMessage = exception.getFieldErrors().get(0).getDefaultMessage();

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", errorMessage);

        if(errorMessage.equals(INVALID_STORE_ADDRESS_FORMAT_MESSAGE)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDto(INVALID_STORE_ADDRESS_FORMAT_CODE, INVALID_STORE_ADDRESS_FORMAT_MESSAGE));
        }
        if(errorMessage.equals(INVALID_STORE_NAME_FORMAT_MESSAGE)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDto(INVALID_STORE_NAME_FORMAT_CODE, INVALID_STORE_NAME_FORMAT_MESSAGE));
        }
        if(errorMessage.equals(INVALID_BUSINESS_LICENSE_CODE_FORMAT_MESSAGE)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDto(INVALID_BUSINESS_LICENSE_CODE_FORMAT_CODE, INVALID_BUSINESS_LICENSE_CODE_FORMAT_MESSAGE));
        }
        throw new IllegalStateException();
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
