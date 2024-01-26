package com.ssg.usms.business.user.constant;

import java.util.HashMap;

public class UserConstants {
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String NICKNAME_PATTERN = "[a-zA-Z0-9]{1,10}";
    public static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*[@#$%^&+=!*])(?=.*\\d).{8,30}$";
    public static final String USERNAME_PATTERN = "[a-zA-Z0-9]{5,20}";
    public static final String PHONENUMBER_PATTERN = "^010-\\d{4}-\\d{4}";

    public static final String SECONDPASSWORD_PATTERN = "^(?!.*(\\d)(?:.*\\1){2})\\d{6}$";


    public static final String NOT_ALLOWED_KEY_LITERAL = "잘못된 본인인증 키입니다.";

    public static final String NOT_ALLOWED_EMAIL_FORM_LITERAL = "유효하지 않은 이메일 형식입니다.";

    public static final String NOT_ALLOWED_NICKNAME_FORM_LITERAL = "닉네임은 1자이상 10자이하 특수문자를 포함할수없습니다.";

    public static final String NOT_ALLOWED_PASSWORD_FORM_LITERAL = "비밀 번호의 길이는 최소 8자 이상 최대 30자 이하이고 문자, 숫자, 특수문자가 적어도 하나 이상 포함되어야 합니다.";

    public static final String NOT_ALLOWED_USERNAME_FORM_LITERAL = "아이디의 길이는 최소 5자 이상 최대 20자 이하, 특수문자가 포함되어서는 안됩니다.";

    public static final String NOT_ALLOWED_PHONENUMBER_FORM_LITERAL = "전화번호 형식이 일치하지않습니다.";

    public static final String INTERNAL_SERVER_ERROR_LITERAL = "Internal Server Error";

    public static final String ALREADY_EXISTS_USER_LITERAL = "이미 존재하는 유저정보입니다.";

    public static final String ALREADY_EXISTS_USERNAME_LITERAL = "이미 존재하는 아이디입니다.";

    public static final String ALREADY_EXISTS_EMAIL_LITERAL = "이미 존재하는 이메일 입니다.";

    public static final String ALREADY_EXISTS_PHONE_LITERAL = "이미 존재하는 전화번호입니다.";

    public static final String NOT_ALLOWED_FORM_LITERAL = "잘못된 양식으로 요청했습니다.";

    public static final String NOT_ALLOWED_SECONDPASSWORD_LITERAL = "올바르지 못한 2차 비밀번호 양식입니다.";

    public static final String NOT_MATCHED_SESSION_USRID_LITERAL = "요청한 유저 id와 실제 유저 id가 다릅니다.";

    public static final String NOT_ALLOWED_SESSION_LITERAL = "허용되지 않은 세션 상태입니다.";

    public static final String NOT_ALLOWED_SECURITY_LITERAL = "허용되지 않은 보안 레벨 코드입니다.";

    public static final String NOT_ALLOWED_BODY = "값이 누락되어 전달되었습니다.";

    public static final String NOT_MATCHED_JWT_DTO_LITERAL = "인증된 값과 제공된 값이 다릅니다.";

    public static final String NOT_EXISTING_USER_IN_SESSION_LITERAL = "존재하지 않는 유저 정보입니다.";









}
