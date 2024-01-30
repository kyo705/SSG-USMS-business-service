package com.ssg.usms.business.accident.constant;

public class AccidentConstants {

    public static final String DATE_FORMAT_REGEX = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";

    public static final String ACCIDENT_OCCURRENCE_SUBJECT = "[이상 행동 발생]";

    public static final String ACCIDENT_OCCURRENCE_MESSAGE = "매장 : %s 에서 이상 행동 %s 가 발생했습니다.";
}
