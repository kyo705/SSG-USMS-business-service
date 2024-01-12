package com.ssg.usms.business.video.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_OWNED_STREAM_KEY;

public class NotOwnedStreamKeyException extends IllegalStreamKeyException {

    public NotOwnedStreamKeyException(String message) {

        super(NOT_OWNED_STREAM_KEY, message);
    }
}
