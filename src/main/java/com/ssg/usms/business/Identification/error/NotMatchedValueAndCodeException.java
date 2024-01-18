package com.ssg.usms.business.Identification.error;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_MATCHED_CODE_VALUE;

public class NotMatchedValueAndCodeException extends IllegalIndificationFormException{
    public NotMatchedValueAndCodeException(String message){

        super(NOT_MATCHED_CODE_VALUE, message);
    }
}
