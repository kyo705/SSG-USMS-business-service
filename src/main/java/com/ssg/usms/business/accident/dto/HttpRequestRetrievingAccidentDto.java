package com.ssg.usms.business.accident.dto;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ssg.usms.business.accident.constant.AccidentConstants.DATE_FORMAT_REGEX;
import static com.ssg.usms.business.constant.CustomStatusCode.*;

@Getter
@Setter
public class HttpRequestRetrievingAccidentDto {

    private List<AccidentBehavior> behavior = new ArrayList<>();

    @NotNull(message = INVALID_DATE_FORMAT_MESSAGE)
    @Pattern(regexp = DATE_FORMAT_REGEX, message = INVALID_DATE_FORMAT_MESSAGE)
    private String startDate;

    @NotNull(message = INVALID_DATE_FORMAT_MESSAGE)
    @Pattern(regexp = DATE_FORMAT_REGEX, message = INVALID_DATE_FORMAT_MESSAGE)
    private String endDate;

    @NotNull(message = NOT_ALLOWED_PAGE_OFFSET_FORMAT_MESSAGE)
    @PositiveOrZero(message = NOT_ALLOWED_PAGE_OFFSET_FORMAT_MESSAGE)
    private Integer offset;

    @NotNull(message = NOT_ALLOWED_PAGE_SIZE_FORMAT_MESSAGE)
    @Positive(message = NOT_ALLOWED_PAGE_SIZE_FORMAT_MESSAGE)
    private Integer size;

}
