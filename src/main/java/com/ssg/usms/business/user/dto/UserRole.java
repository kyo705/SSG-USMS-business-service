package com.ssg.usms.business.user.dto;

import lombok.Getter;

@Getter
public enum UserRole {
    ROLE_ADMIN(0)
    ,ROLE_STORE_OWNER(1);

    private final int code;

    UserRole(int code){
        this.code = code;
    }
}
