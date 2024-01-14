package com.ssg.usms.business.video.exception;

import com.ssg.usms.business.constant.CustomStatusCode;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.video.constant.VideoConstants;
import lombok.Getter;

import static com.ssg.usms.business.constant.CustomStatusCode.ALREADY_CONNECTED_STREAM_KEY;

@Getter
public class AlreadyConnectedStreamKeyException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto;

    public AlreadyConnectedStreamKeyException() {

        errorResponseDto = new ErrorResponseDto(ALREADY_CONNECTED_STREAM_KEY, "이미 해당 키에 연결된 스트림이 존재합니다.");
    }
}
