package com.ssg.usms.business.accident.handler;

import com.ssg.usms.business.accident.controller.AccidentController;
import com.ssg.usms.business.accident.exception.InvalidDateFlowException;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.store.exception.NotExistingStoreException;
import com.ssg.usms.business.store.exception.NotOwnedStoreException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice(assignableTypes = AccidentController.class)
public class AccidentExceptionHandler {

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        String errorMessage = exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", errorMessage);

        if(errorMessage.equals(INVALID_STREAM_KEY_FORMAT_MESSAGE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDto(INVALID_STREAM_KEY_FORMAT_CODE, INVALID_STREAM_KEY_FORMAT_MESSAGE));
        }
        if(errorMessage.equals(NOT_EXISTING_ACCIDENT_BEHAVIOR_MESSAGE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDto(NOT_EXISTING_ACCIDENT_BEHAVIOR_CODE, NOT_EXISTING_ACCIDENT_BEHAVIOR_MESSAGE));
        }
        if(errorMessage.equals(INVALID_TIMESTAMP_FORMAT_MESSAGE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDto(INVALID_TIMESTAMP_FORMAT_CODE, INVALID_TIMESTAMP_FORMAT_MESSAGE));
        }
        throw new IllegalStateException();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {

        String errorMessage = exception.getMessage();

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", errorMessage);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(400, "잘못된 파라미터 요청입니다."));

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
