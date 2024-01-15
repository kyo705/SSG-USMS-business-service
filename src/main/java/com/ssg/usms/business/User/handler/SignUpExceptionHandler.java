package com.ssg.usms.business.User.handler;


import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.User.controller.SignupController;
import com.ssg.usms.business.User.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = {SignupController.class})
public class SignUpExceptionHandler {

    @ExceptionHandler(AlreadyExistIdException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyExistIdException (AlreadyExistIdException exception){

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getErrorResponseDto());
    }

    @ExceptionHandler(AlreadyExistPhoneNumException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyExistPhoneNumException (AlreadyExistPhoneNumException exception){

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getErrorResponseDto());
    }


    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponseDto> handleExpiredJwtException (ExpiredJwtException exception){

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        throw new NotAllowedKeyExcetpion("잘못된 본인인증 키 입니다.");
    }

    @ExceptionHandler(Exception.class) // IllegalSignUpFormException을 상속받지 않은 모든 예외를 처리
    public ResponseEntity<ErrorResponseDto> handleOtherExceptions(Exception exception) {
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponseDto);
    }


}
