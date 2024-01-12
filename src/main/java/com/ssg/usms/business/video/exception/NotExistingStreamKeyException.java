package com.ssg.usms.business.video.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_EXISTING_STREAM_KEY;

public class NotExistingStreamKeyException extends IllegalStreamKeyException {

    public NotExistingStreamKeyException() {

        super(NOT_EXISTING_STREAM_KEY, "유효하지 않은 스트림 키 값입니다.");
    }
}
