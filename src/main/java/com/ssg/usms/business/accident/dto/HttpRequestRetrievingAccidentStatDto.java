package com.ssg.usms.business.accident.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

import static com.ssg.usms.business.accident.constant.AccidentConstants.DATE_FORMAT_REGEX;
import static com.ssg.usms.business.constant.CustomStatusCode.INVALID_DATE_FORMAT_MESSAGE;

@Getter
@Setter
public class HttpRequestRetrievingAccidentStatDto {

    @Pattern(regexp = DATE_FORMAT_REGEX, message = INVALID_DATE_FORMAT_MESSAGE)
    private String startDate;

    @Pattern(regexp = DATE_FORMAT_REGEX, message = INVALID_DATE_FORMAT_MESSAGE)
    private String endDate;
}
