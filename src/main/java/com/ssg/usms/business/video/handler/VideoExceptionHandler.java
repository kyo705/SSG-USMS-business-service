package com.ssg.usms.business.video.handler;

import com.ssg.usms.business.error.ErrorResponseDto;
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
}
