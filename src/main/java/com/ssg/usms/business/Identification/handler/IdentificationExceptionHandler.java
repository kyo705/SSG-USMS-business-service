package com.ssg.usms.business.Identification.handler;



import com.ssg.usms.business.Identification.contorller.IdentificationController;
import com.ssg.usms.business.Identification.error.NotIdentificationException;
import com.ssg.usms.business.Identification.error.NotMatchedValueAndCodeException;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.notification.exception.NotificationFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.ssg.usms.business.Identification.constant.IdenticationConstant.*;
import static com.ssg.usms.business.constant.CustomStatusCode.*;

@Slf4j
@RestControllerAdvice(assignableTypes = IdentificationController.class)
public class IdentificationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException (MethodArgumentNotValidException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}",   exception.getMessage());


        if (exception.getBindingResult().getAllErrors().get(0).getDefaultMessage().equals(NOT_ALLOWED_VERIFICATION_CODE_LITERAL)) {

            ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_CODE_FORM, NOT_ALLOWED_VERIFICATION_CODE_LITERAL);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponseDto);
        }


        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponseDto);
    }


    @ExceptionHandler(NotMatchedValueAndCodeException.class)
    public ResponseEntity<ErrorResponseDto> handleNotMatchedValueAndCodeException (NotMatchedValueAndCodeException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}",   exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_MATCHED_CODE_VALUE, NOT_MATCHED_CODE_AND_VALUE_LITERAL);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponseDto);
    }

    @ExceptionHandler(NotIdentificationException.class)
    public ResponseEntity<ErrorResponseDto> handleNotIdentificationException (NotIdentificationException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}",   exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), INVALID_AUTHENTICATION_CODE_LITERAL);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponseDto);
    }


    @ExceptionHandler(NotificationFailureException.class)
    public ResponseEntity<ErrorResponseDto> handleNotificationException(NotificationFailureException exception) {
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_SEND_MESSAGE);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(errorResponseDto);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleOtherExceptions(Exception exception) {
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(BAD_GATEWAY_IN_IDENTIFICATION, BAD_GATEWAY_SERVER_LITERAL);

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(errorResponseDto);
    }


}
