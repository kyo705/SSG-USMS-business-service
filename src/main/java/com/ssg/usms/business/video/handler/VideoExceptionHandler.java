package com.ssg.usms.business.video.handler;

import com.ssg.usms.business.cctv.exception.NotExistingCctvException;
import com.ssg.usms.business.cctv.exception.NotOwnedCctvException;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.store.exception.NotOwnedStoreException;
import com.ssg.usms.business.store.exception.UnavailableStoreException;
import com.ssg.usms.business.video.controller.StreamKeyController;
import com.ssg.usms.business.video.controller.VideoController;
import com.ssg.usms.business.video.exception.AlreadyConnectedStreamKeyException;
import com.ssg.usms.business.video.exception.IllegalStreamKeyException;
import com.ssg.usms.business.video.exception.NotAllowedStreamingProtocolException;
import com.ssg.usms.business.video.exception.NotMatchingStreamingProtocolAndFileFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(assignableTypes = {VideoController.class, StreamKeyController.class})
public class VideoExceptionHandler {

    @ExceptionHandler(IllegalStreamKeyException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalStreamKeyException(IllegalStreamKeyException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorResponseDto());
    }

    @ExceptionHandler(NotAllowedStreamingProtocolException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalStreamingProtocolException(NotAllowedStreamingProtocolException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorResponseDto());
    }

    @ExceptionHandler(NotMatchingStreamingProtocolAndFileFormatException.class)
    public ResponseEntity<ErrorResponseDto> handleNotMatchingStreamingProtocolAndFileFormatException(NotMatchingStreamingProtocolAndFileFormatException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorResponseDto());
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

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponseDto.builder().code(400).message(errorMessage).build()
                );
    }

    @ExceptionHandler(AlreadyConnectedStreamKeyException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyConnectedStreamKeyException(AlreadyConnectedStreamKeyException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getMessage());

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

    @ExceptionHandler({UnavailableStoreException.class})
    public ResponseEntity<ErrorResponseDto> handleUnavailableStoreException(UnavailableStoreException exception) {

        log.error("Exception [Err_Location] : {}", exception.getStackTrace().length == 0 ? exception.getClass() : exception.getStackTrace()[0]);
        log.error("Exception [Err_Msg] : {}", exception.getErrorResponseDto());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
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
