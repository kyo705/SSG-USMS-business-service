package com.ssg.usms.business.video.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_OWNED_STREAM_KEY;

public class NotOwnedStreamKeyException extends IllegalStreamKeyException {

    public NotOwnedStreamKeyException() {

        super(NOT_OWNED_STREAM_KEY, "유효하지 않은 스트림 키 값입니다.");
    }
}
