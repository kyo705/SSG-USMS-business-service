package com.ssg.usms.business.store.dto;

import com.ssg.usms.business.store.constant.StoreState;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static com.ssg.usms.business.store.constant.StoreConstants.BUSINESS_LICENSE_CODE_REGEX;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpRequestRetrievingStoreDto {

    private Long user;

    private StoreState storeState;

    @Pattern(regexp = BUSINESS_LICENSE_CODE_REGEX, message = INVALID_BUSINESS_LICENSE_CODE_FORMAT_MESSAGE)
    private String businessLicenseCode;

    @NotNull(message = NOT_ALLOWED_PAGE_OFFSET_FORMAT_MESSAGE)
    @PositiveOrZero(message = NOT_ALLOWED_PAGE_OFFSET_FORMAT_MESSAGE)
    private Long storeId;

    @NotNull(message = NOT_ALLOWED_PAGE_SIZE_FORMAT_MESSAGE)
    @Positive(message = NOT_ALLOWED_PAGE_SIZE_FORMAT_MESSAGE)
    private Integer size;

}
