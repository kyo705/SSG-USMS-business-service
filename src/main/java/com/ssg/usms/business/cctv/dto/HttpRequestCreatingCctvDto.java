package com.ssg.usms.business.cctv.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.ssg.usms.business.cctv.constant.CctvConstants.CCTV_NAME_REGEX;
import static com.ssg.usms.business.constant.CustomStatusCode.INVALID_CCTV_NAME_FORMAT_MESSAGE;

@Getter
@Setter
public class HttpRequestCreatingCctvDto {

    @NotBlank(message = INVALID_CCTV_NAME_FORMAT_MESSAGE)
    @Pattern(regexp = CCTV_NAME_REGEX, message = INVALID_CCTV_NAME_FORMAT_MESSAGE)
    private String name;
}
