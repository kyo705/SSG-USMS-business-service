package com.ssg.usms.business.video.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_EXISTING_STREAM_KEY;

public class NotExistingStreamKeyException extends IllegalStreamKeyException {

    public NotExistingStreamKeyException(String message) {

        super(NOT_EXISTING_STREAM_KEY, message);
    }
}
