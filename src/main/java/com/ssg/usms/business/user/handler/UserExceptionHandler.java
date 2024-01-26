package com.ssg.usms.business.user.handler;


import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.user.controller.UserController;
import com.ssg.usms.business.user.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static com.ssg.usms.business.user.constant.UserConstants.*;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class})
public class UserExceptionHandler {

    @ExceptionHandler(AlreadyExistUsernameException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyExistIdException (AlreadyExistUsernameException exception){

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ALREADY_EXIST_USERNAME, NOT_ALLOWED_USERNAME_FORM_LITERAL);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponseDto);
    }

    @ExceptionHandler(AlreadyExistPhoneNumException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyExistPhoneNumException (AlreadyExistPhoneNumException exception){

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ALREADY_EXIST_PHONE_NUM, NOT_ALLOWED_PHONENUMBER_FORM_LITERAL);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponseDto);
    }

    @ExceptionHandler({ExpiredJwtException.class , NotAllowedKeyExcetpion.class, SignatureException.class})
    public ResponseEntity<ErrorResponseDto> handleExpiredJwtException (Exception exception){

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_KEY, NOT_ALLOWED_KEY_LITERAL);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponseDto);
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException (ValidationException exception){

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), NOT_ALLOWED_BODY);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponseDto);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException (MethodArgumentNotValidException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}",   exception.getMessage());


        if (exception.getBindingResult().getAllErrors().get(0).getDefaultMessage().equals(NOT_ALLOWED_EMAIL_FORM_LITERAL)) {

            ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_EMAIL_FORM, NOT_ALLOWED_EMAIL_FORM_LITERAL);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponseDto);
        }
        if (exception.getBindingResult().getAllErrors().get(0).getDefaultMessage().equals(NOT_ALLOWED_NICKNAME_FORM_LITERAL)) {

            ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_NICKNAME_FORM, NOT_ALLOWED_NICKNAME_FORM_LITERAL);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponseDto);
        }
        if (exception.getBindingResult().getAllErrors().get(0).getDefaultMessage().equals(NOT_ALLOWED_PASSWORD_FORM_LITERAL)) {

            ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_PASSWORD_FORM, NOT_ALLOWED_PASSWORD_FORM_LITERAL);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponseDto);
        }
        if (exception.getBindingResult().getAllErrors().get(0).getDefaultMessage().equals(NOT_ALLOWED_USERNAME_FORM_LITERAL)) {

            ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_USERNAME_FORM, NOT_ALLOWED_USERNAME_FORM_LITERAL);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponseDto);
        }
        if (exception.getBindingResult().getAllErrors().get(0).getDefaultMessage().equals(NOT_ALLOWED_PHONENUMBER_FORM_LITERAL )) {

            ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_PHONENUMBER_FORM, NOT_ALLOWED_PHONENUMBER_FORM_LITERAL );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponseDto);
        }
        if (exception.getBindingResult().getAllErrors().get(0).getDefaultMessage().equals(NOT_ALLOWED_SECURITY_LITERAL )) {

            ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), NOT_ALLOWED_SECURITY_LITERAL );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponseDto);
        }


        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), NOT_ALLOWED_BODY);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponseDto);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException (DataIntegrityViolationException exception) {
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.CONFLICT.value(), ALREADY_EXISTS_USER_LITERAL);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponseDto);
    }

    @ExceptionHandler(NotAllowedSessionIdException.class)
    public ResponseEntity<ErrorResponseDto> handleNotAllowedSessionIdException(NotAllowedSessionIdException exception) {
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(BAD_REQUEST_SESSIONID, ALREADY_EXISTS_USER_LITERAL);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponseDto);
    }

    @ExceptionHandler(NotAllowedFormCheckException.class)
    public ResponseEntity<ErrorResponseDto> handleNotAllowedFormCheckException(NotAllowedFormCheckException exception) {
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), NOT_ALLOWED_FORM_LITERAL);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(errorResponseDto);
    }

    @ExceptionHandler(AlreadyExistEmailException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyExistEmailException(AlreadyExistEmailException exception) {
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.CONFLICT.value(), ALREADY_EXISTS_EMAIL_LITERAL);
        return ResponseEntity
                .status(HttpStatus.CONFLICT.value())
                .body(errorResponseDto);
    }

    @ExceptionHandler(NotAllowedSecondPasswordException.class)
    public ResponseEntity<ErrorResponseDto> handleNotAllowedSecondPasswordException(NotAllowedSecondPasswordException exception) {
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), NOT_ALLOWED_SECONDPASSWORD_LITERAL);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(errorResponseDto);
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalAccessException(IllegalAccessException exception) {
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), NOT_MATCHED_SESSION_USRID_LITERAL);

         return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED.value())
                .body(errorResponseDto);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(),exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(errorResponseDto);
    }

    @ExceptionHandler(NotMatchedDtoJwtValueException.class)
    public ResponseEntity<ErrorResponseDto> handleNotMatchedDtoJwtValueException(NotMatchedDtoJwtValueException exception) {
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(),NOT_MATCHED_JWT_DTO_LITERAL);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(errorResponseDto);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleOtherExceptions(Exception exception) {
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), INTERNAL_SERVER_ERROR_LITERAL);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponseDto);
    }


}
