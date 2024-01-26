package com.ssg.usms.business.user.dto;

public enum SecurityState {
    BASIC(0),
    SMS(1),
    SECONDPASSWORD(2);
    private final int level;
    SecurityState(int level){
        this.level = level;
    }

    public int getLevel(){
        return this.level;
    }
}
