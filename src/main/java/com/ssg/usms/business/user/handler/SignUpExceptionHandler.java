package com.ssg.usms.business.user.handler;


import com.ssg.usms.business.user.controller.SignupController;
import com.ssg.usms.business.user.exception.AlreadyExistIdException;
import com.ssg.usms.business.user.exception.AlreadyExistPhoneNumException;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.user.exception.NotAllowedKeyExcetpion;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import static com.ssg.usms.business.constant.CustomStatusCode.*;

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

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_KEY, "잘못된 본인인증 키입니다.");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponseDto);
    }

    @ExceptionHandler(NotAllowedKeyExcetpion.class)
    public ResponseEntity<ErrorResponseDto> handleNotAllowedKeyException (NotAllowedKeyExcetpion exception){

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_KEY, "잘못된 본인인증 키입니다.");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponseDto);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException (MethodArgumentNotValidException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}",   exception.getMessage());


        if (exception.getBindingResult().getAllErrors().get(0).getDefaultMessage().equals("유효하지 않은 이메일 형식입니다.")) {

            ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_EMAIL_FORM, "유효하지 않은 이메일 형식입니다.");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponseDto);
        }
        if (exception.getBindingResult().getAllErrors().get(0).getDefaultMessage().equals("닉네임은 1자이상 10자이하 특수문자를 포함할수없습니다.")) {

            ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_NICKNAME_FORM, "닉네임은 1자이상 10자이하 특수문자를 포함할수없습니다.");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponseDto);
        }
        if (exception.getBindingResult().getAllErrors().get(0).getDefaultMessage().equals("비밀 번호의 길이는 최소 8자 이상 최대 30자 이하이고 문자, 숫자, 특수문자가 적어도 하나 이상 포함되어야 합니다.")) {

            ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_PASSWORD_FORM, "비밀 번호의 길이는 최소 8자 이상 최대 30자 이하이고 문자, 숫자, 특수문자가 적어도 하나 이상 포함되어야 합니다.");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponseDto);
        }
        if (exception.getBindingResult().getAllErrors().get(0).getDefaultMessage().equals("아이디의 길이는 최소 5자 이상 최대 20자 이하, 특수문자가 포함되어서는 안됩니다.")) {

            ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_ID_FORM, "아이디의 길이는 최소 5자 이상 최대 20자 이하, 특수문자가 포함되어서는 안됩니다.");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponseDto);
        }
        if (exception.getBindingResult().getAllErrors().get(0).getDefaultMessage().equals("전화번호 형식이 일치하지않습니다.")) {

            ErrorResponseDto errorResponseDto = new ErrorResponseDto(NOT_ALLOWED_PHONENUMBER_FORM, "전화번호 형식이 일치하지않습니다.");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponseDto);
        }

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponseDto);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException (DataIntegrityViolationException exception) {
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.CONFLICT.value(), "이미 존재하는 유저정보입니다.");

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponseDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleOtherExceptions(Exception exception) {
        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponseDto);
    }


}
