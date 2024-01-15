package com.ssg.usms.business.User.handler;


import com.ssg.usms.business.User.exception.*;
import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionController {
    @ExceptionHandler(NotAllowedIdFormException.class)
    public ResponseEntity<ErrorResponseDto> handleNotAllowedIdFormException(NotAllowedIdFormException exception){

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorResponseDto());
    }

    @ExceptionHandler(NotAllowedPassWordFormException.class)
    public ResponseEntity<ErrorResponseDto> handleNotAllowedIdFormException(NotAllowedPassWordFormException exception){

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorResponseDto());
    }

    @ExceptionHandler(NotAllowedEmailFormException.class)
    public ResponseEntity<ErrorResponseDto> handleNotAllowedEmailFormException(NotAllowedEmailFormException exception){

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorResponseDto());
    }

    @ExceptionHandler(NotAllowedKeyExcetpion.class)
    public ResponseEntity<ErrorResponseDto> handleNotAllowedKeyException(NotAllowedKeyExcetpion exception){

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(exception.getErrorResponseDto());
    }

    @ExceptionHandler(NotAllowedPhoneNumberFormException.class)
    public ResponseEntity<ErrorResponseDto> NotAllowedPhoneNumFormExcetpion (NotAllowedPhoneNumberFormException exception){

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getErrorResponseDto());
    }

    @ExceptionHandler(NotAllowedNickNameFormException.class)
    public ResponseEntity<ErrorResponseDto> NotAllowedNickNameFormException (NotAllowedNickNameFormException exception){

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getErrorResponseDto());
    }
}
