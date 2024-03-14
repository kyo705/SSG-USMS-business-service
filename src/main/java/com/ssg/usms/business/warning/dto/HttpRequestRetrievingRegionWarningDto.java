package com.ssg.usms.business.warning.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static com.ssg.usms.business.accident.constant.AccidentConstants.DATE_FORMAT_REGEX;
import static com.ssg.usms.business.constant.CustomStatusCode.*;

@Getter
@Setter
public class HttpRequestRetrievingRegionWarningDto {

    @Pattern(regexp = DATE_FORMAT_REGEX, message = INVALID_DATE_FORMAT_MESSAGE)
    private String startDate;

    @Pattern(regexp = DATE_FORMAT_REGEX, message = INVALID_DATE_FORMAT_MESSAGE)
    private String endDate;

    @NotNull(message = NOT_ALLOWED_PAGE_OFFSET_FORMAT_MESSAGE)
    @PositiveOrZero(message = NOT_ALLOWED_PAGE_OFFSET_FORMAT_MESSAGE)
    private Long regionWarningId;

    @NotNull(message = NOT_ALLOWED_PAGE_SIZE_FORMAT_MESSAGE)
    @Positive(message = NOT_ALLOWED_PAGE_SIZE_FORMAT_MESSAGE)
    private Integer size;
}
