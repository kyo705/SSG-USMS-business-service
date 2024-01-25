package com.ssg.usms.business.accident.dto;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.video.annotation.StreamKey;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static com.ssg.usms.business.constant.CustomStatusCode.*;

@Getter
@Setter
public class HttpRequestCreatingAccidentDto {

    @NotNull(message = INVALID_STREAM_KEY_FORMAT_MESSAGE)
    @StreamKey
    private String streamKey;

    @NotNull(message = NOT_EXISTING_ACCIDENT_BEHAVIOR_MESSAGE)
    private AccidentBehavior behavior;

    @NotNull(message = INVALID_TIMESTAMP_FORMAT_MESSAGE)
    @Positive(message = INVALID_TIMESTAMP_FORMAT_MESSAGE)
    private Long startTimestamp;
}
