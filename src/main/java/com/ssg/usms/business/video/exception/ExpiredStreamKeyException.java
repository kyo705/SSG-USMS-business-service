package com.ssg.usms.business.video.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.EXPIRED_STREAM_KEY;

public class ExpiredStreamKeyException extends IllegalStreamKeyException {

    public ExpiredStreamKeyException(String message) {

        super(EXPIRED_STREAM_KEY, message);
    }
}
