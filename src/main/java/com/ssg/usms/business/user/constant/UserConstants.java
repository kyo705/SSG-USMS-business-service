package com.ssg.usms.business.user.constant;

public class UserConstants {
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final String NICKNAME_PATTERN = "[a-zA-Z0-9]{1,10}";

    public static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,30}$";

    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9]{5,20}$";

    public static final String PHONENUMBER_PATTERN = "^010-\\d{4}-\\d{4}$";

}
