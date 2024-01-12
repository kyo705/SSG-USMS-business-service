package com.ssg.usms.business.video.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.EXPIRED_STREAM_KEY;

public class ExpiredStreamKeyException extends IllegalStreamKeyException {

    public ExpiredStreamKeyException() {

        super(EXPIRED_STREAM_KEY, "유효하지 않은 스트림 키 값입니다.");
    }
}
