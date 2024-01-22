package com.ssg.usms.business.Identification.constant;

public class IdenticationConstant {

    public static final String SUCCESS_SEND_VERIFICATION_CODE_LITERAL = "인증번호 발송 성공";
    public static final String NOT_ALLOWED_VERIFICATION_CODE_LITERAL = "존재하지 않는 본인 인증 방식입니다.";
    public static final String NOT_MATCHED_CODE_AND_VALUE_LITERAL = "선택한 본인 인증방식에 부적절한 양식입니다.";

    public static final String BAD_GATEWAY_SERVER_LITERAL = "알림 전송 중 예외가 발생했습니다.";
    public static final String INVALID_AUTHENTICATION_CODE_LITERAL= "잘못된 인증 번호입니다.";
    public static final String IDENTIFICATION_SUBJECT = "본인인증";

    public static final String ERROR_SEND_MESSAGE = "본인인증 코드 전송에 실패하였습니다.";

    public static final String IDENTIFICATION_HEADER = "x-authenticate-key";

    public static final String IDENTIFICATION_SUCCESS_MESSAGE = "인증에 성공했습니다.";


}
