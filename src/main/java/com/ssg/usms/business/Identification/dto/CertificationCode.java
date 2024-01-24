package com.ssg.usms.business.Identification.dto;

public enum CertificationCode {
    EMAIL(0),
    SMS(1);

    private final int code;

    CertificationCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
