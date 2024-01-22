package com.ssg.usms.business.constant;

public class CustomStatusCode {

    public static final int NOT_MATCHING_STREAM_PROTOCOL_AND_FILE_FORMAT = 628;
    public static final int NOT_ALLOWED_STREAM_PROTOCOL = 627;
    public static final int NOT_OWNED_STREAM_KEY = 626;
    public static final int NOT_EXISTING_STREAM_KEY = 625;
    public static final int EXPIRED_STREAM_KEY = 624;
    public static final int ALREADY_CONNECTED_STREAM_KEY = 629;

    public static final int INVALID_STORE_NAME_FORMAT_CODE = 623;
    public static final String INVALID_STORE_NAME_FORMAT_MESSAGE = "허용되지 않은 매장명 양식입니다.";

    public static final int INVALID_STORE_ADDRESS_FORMAT_CODE = 622;
    public static final String INVALID_STORE_ADDRESS_FORMAT_MESSAGE = "허용되지 않은 매장 주소 양식입니다.";

    public static final int INVALID_BUSINESS_LICENSE_CODE_FORMAT_CODE = 618;
    public static final String INVALID_BUSINESS_LICENSE_CODE_FORMAT_MESSAGE = "허용되지 않은 사업자 등록 번호 양식입니다.";

    public static final int INVALID_BUSINESS_LICENSE_IMG_KEY_FORMAT_CODE = 630;
    public static final String INVALID_BUSINESS_LICENSE_IMG_KEY_FORMAT_MESSAGE = "허용되지 않은 사업자 등록증 사본 이미지 키 양식입니다.";

    public static final int NOT_ALLOWED_IMG_FILE_FORMAT_CODE = 616;
    public static final String NOT_ALLOWED_IMG_FILE_FORMAT_MESSAGE = "허용되지 않은 파일 확장자 명입니다.";

    public static final int EMPTY_IMG_FILE_CODE = 619;
    public static final String EMPTY_IMG_FILE_MESSAGE = "사업자 등록증 사본을 반드시 업로드해야 합니다.";

    public static final int NOT_ALLOWED_STORE_STATE_FORMAT_CODE = 631;
    public static final String NOT_ALLOWED_STORE_STATE_FORMAT_MESSAGE = "허용되지 않은 매장 상태 정보입니다.";

    public static final int NOT_OWNED_BUSINESS_LICENSE_IMG_KEY_CODE = 632;
    public static final String NOT_OWNED_BUSINESS_LICENSE_IMG_KEY_MESSAGE = "해당 매장이 보유하지 않은 사업자등록증 사본 이미지 키 값입니다.";

    public static final int NOT_EXISTING_STORE_CODE = 633;
    public static final String NOT_EXISTING_STORE_MESSAGE = "존재하지 않은 매장입니다.";

    public static final int NOT_OWNED_STORE_CODE = 634;
    public static final String NOT_OWNED_STORE_MESSAGE = "본인 소유의 매장이 아닙니다.";

    public static final int NOT_ALLOWED_PAGE_OFFSET_FORMAT_CODE = 635;
    public static final String NOT_ALLOWED_PAGE_OFFSET_FORMAT_MESSAGE = "페이지 offset 값은 반드시 0이상이어야 합니다.";

    public static final int NOT_ALLOWED_PAGE_SIZE_FORMAT_CODE = 636;
    public static final String NOT_ALLOWED_PAGE_SIZE_FORMAT_MESSAGE = "페이지 size 값은 반드시 0보다 커야 합니다.";

}
