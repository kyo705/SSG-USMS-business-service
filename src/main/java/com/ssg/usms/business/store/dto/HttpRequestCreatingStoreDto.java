package com.ssg.usms.business.store.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static com.ssg.usms.business.store.constant.StoreConstants.*;

@ToString
@Getter
@Setter
public class HttpRequestCreatingStoreDto {

    @NotBlank(message = INVALID_STORE_NAME_FORMAT_MESSAGE)
    @Pattern(regexp = STORE_NAME_REGEX, message = INVALID_STORE_NAME_FORMAT_MESSAGE)
    private String storeName;

    @NotBlank(message = INVALID_STORE_ADDRESS_FORMAT_MESSAGE)
    @Pattern(regexp = STORE_ADDRESS_REGEX, message = INVALID_STORE_ADDRESS_FORMAT_MESSAGE)
    private String storeAddress;

    @NotBlank(message = INVALID_BUSINESS_LICENSE_CODE_FORMAT_MESSAGE)
    @Pattern(regexp = BUSINESS_LICENSE_CODE_REGEX, message = INVALID_BUSINESS_LICENSE_CODE_FORMAT_MESSAGE)
    private String businessLicenseCode;
}
