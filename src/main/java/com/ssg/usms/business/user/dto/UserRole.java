package com.ssg.usms.business.user.dto;

import lombok.Getter;

@Getter
public enum UserRole {
    ROLE_ADMIN(0, "ADMIN"),
    ROLE_STORE_OWNER(1, "STORE_OWNER");

    private final int code;
    private final String role;

    UserRole(int code, String role){
        this.code = code;
        this.role = role;
    }
}
