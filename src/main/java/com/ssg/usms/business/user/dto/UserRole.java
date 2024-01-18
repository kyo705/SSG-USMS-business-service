package com.ssg.usms.business.user.dto;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN(0)
    ,STORE_OWNER(1);

    private final int code;

    UserRole(int code){
        this.code = code;
    }
}
