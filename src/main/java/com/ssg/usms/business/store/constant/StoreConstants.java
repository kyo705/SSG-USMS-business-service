package com.ssg.usms.business.store.constant;

import java.util.Set;

public class StoreConstants {

    public static final String BUSINESS_LICENSE_CODE_REGEX = "^\\d{3}-\\d{2}-\\d{5}$";
    public static final String STORE_NAME_REGEX = "^[가-힣a-zA-Z0-9\\s]{1,20}$";
    public static final String STORE_ADDRESS_REGEX = "^[가-힣a-zA-Z0-9\\s]{1,50}$";

    public static final Set<String> ALLOWED_IMG_FILE_FORMATS = Set.of("pdf");
}
