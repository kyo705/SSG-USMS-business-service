package com.ssg.usms.business.user.exception;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_ALLOWED_KEY;

public class NotAllowedKeyExcetpion extends IllegalFormException {

    public NotAllowedKeyExcetpion(String message){

        super(NOT_ALLOWED_KEY, message);
    }

}
